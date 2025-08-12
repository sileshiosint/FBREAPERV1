import logging
import asyncio
from playwright.async_api import async_playwright

class FacebookScraper:
    def __init__(self):
        # You may want to add logic to ensure user-data-dir is set for logged-in session
        self.browser = None
        self.context = None

    async def _init_browser(self):
        playwright = await async_playwright().start()
        # Use a persistent context for an already logged-in session
        self.context = await playwright.chromium.launch_persistent_context(
            user_data_dir="fb_session",
            headless=True,
            slow_mo=0
        )
        self.browser = self.context

    async def scrape_keyword(self, keyword, max_posts=10):
        """
        Search Facebook for a keyword and scrape posts.
        """
        if not self.context:
            await self._init_browser()
        page = await self.context.new_page()
        await page.goto("https://www.facebook.com/")
        await asyncio.sleep(2)
        # Emulate human typing
        await page.fill('input[placeholder="Search Facebook"]', keyword, delay=120)
        await page.keyboard.press('Enter')
        await asyncio.sleep(3)
        await page.wait_for_selector('a[href*="search/posts"]', timeout=6000)
        await page.click('a[href*="search/posts"]')
        await asyncio.sleep(4)

        # Infinite scroll to load posts
        posts_data = []
        last_height = 0
        for _ in range(5):  # Spread across multiple scrolls
            await page.mouse.wheel(0, 1000)
            await asyncio.sleep(2)

        post_elements = await page.query_selector_all('[role="article"]')
        for post_el in post_elements[:max_posts]:
            post_data = await self._parse_post(post_el)
            if post_data:
                posts_data.append(post_data)
        await page.close()
        return posts_data

    async def _parse_post(self, post_el):
        """
        Extracts post, comments, reactions, metadata from a post element.
        """
        try:
            # This XPath/CSS logic may need tuning for Facebook's markup!
            content = await post_el.inner_text('div[dir="auto"]')
            author = await post_el.inner_text('h2 span a, strong span a')
            timestamp = await post_el.get_attribute('abbr[data-utime], span[role="time"]')
            # Optionally parse comments, reactions, etc.
            comments = await self._parse_comments(post_el)
            return {
                "content": content,
                "author": author,
                "timestamp": timestamp,
                "comments": comments
            }
        except Exception as ex:
            logging.error(f"Error parsing post: {ex}")
            return None

    async def _parse_comments(self, post_el):
        # Recursive logic for nested comments
        try:
            comment_elems = await post_el.query_selector_all('[aria-label="Comment"]')
            comments = []
            for c_el in comment_elems:
                text = await c_el.inner_text('div[dir="auto"]')
                author = await c_el.inner_text('a[role="link"]')
                timestamp = await c_el.get_attribute('abbr[data-utime], span[role="time"]')
                # Check for nested replies recursively if needed
                # ...
                comments.append({
                    "text": text,
                    "author": author,
                    "timestamp": timestamp
                })
            return comments
        except Exception as ex:
            logging.error(f"Error parsing comments: {ex}")
            return []
