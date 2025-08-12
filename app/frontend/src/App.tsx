import ScraperControls from './components/ScraperControls'
import Stats from './components/Stats'
import Posts from './components/Posts'

export default function App() {
  return (
    <div className="container mx-auto p-6 space-y-6">
      <header className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Facebook OSINT Dashboard</h1>
      </header>
      <ScraperControls />
      <Stats />
      <Posts />
    </div>
  )
}