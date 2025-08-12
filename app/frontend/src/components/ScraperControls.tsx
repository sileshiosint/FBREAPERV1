import { useState } from 'react'
import api from '../api/client'

export default function ScraperControls() {
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  const startScraper = async () => {
    setLoading(true)
    try {
      await api.post('/scraper/start')
      setMessage('Scraper started')
    } catch (e) {
      setMessage('Failed to start scraper')
    } finally {
      setLoading(false)
    }
  }

  const scrapeByKeyword = async () => {
    if (!keyword) return
    setLoading(true)
    try {
      await api.post('/scraper/scrapeByKeyword', null, { params: { keyword } })
      setMessage(`Requested scrape for "${keyword}"`)
      setKeyword('')
    } catch (e) {
      setMessage('Failed to request scrape')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="p-4 bg-white rounded-lg shadow">
      <h2 className="text-lg font-semibold mb-2">Scraper Controls</h2>
      <div className="flex gap-2">
        <input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="Keyword (e.g., osint)"
          className="border rounded px-3 py-2 flex-1"
        />
        <button onClick={scrapeByKeyword} disabled={loading} className="bg-blue-600 text-white px-4 py-2 rounded">
          Scrape by Keyword
        </button>
        <button onClick={startScraper} disabled={loading} className="bg-gray-700 text-white px-4 py-2 rounded">
          Start Scraper
        </button>
      </div>
      {message && <p className="text-sm text-gray-600 mt-2">{message}</p>}
    </div>
  )
}