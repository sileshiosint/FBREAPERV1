import { useEffect, useState } from 'react'
import api from '../api/client'

export default function Stats() {
  const [stats, setStats] = useState<any>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get('/data/stats')
      .then(res => setStats(res.data))
      .catch(() => setError('Failed to load stats'))
  }, [])

  if (error) return <div className="p-4 bg-white rounded-lg shadow">{error}</div>
  if (!stats) return <div className="p-4 bg-white rounded-lg shadow">Loading stats...</div>

  return (
    <div className="p-4 bg-white rounded-lg shadow grid grid-cols-2 md:grid-cols-3 gap-4">
      <StatCard label="Posts" value={stats.totalPosts} />
      <StatCard label="Comments" value={stats.totalComments} />
      <StatCard label="Reactions" value={stats.totalReactions} />
      <StatCard label="Active Scrapers" value={stats.activeScrapers} />
      <StatCard label="Errors Today" value={stats.errorsToday} />
      <StatCard label="Data Today" value={stats.dataCollectedToday} />
    </div>
  )
}

function StatCard({ label, value }: { label: string, value: any }) {
  return (
    <div className="border rounded p-3">
      <div className="text-sm text-gray-500">{label}</div>
      <div className="text-2xl font-semibold">{value}</div>
    </div>
  )
}