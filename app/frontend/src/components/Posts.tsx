import { useEffect, useState } from 'react'
import api from '../api/client'

interface Post {
  id: string
  author: string
  content: string
  timestamp: string
  hashtags?: string
  language?: string
  sentiment?: string
}

export default function Posts() {
  const [page, setPage] = useState(0)
  const [size] = useState(10)
  const [data, setData] = useState<{ content: Post[]; totalPages: number } | null>(null)

  useEffect(() => {
    api.get('/data/posts', { params: { page, size } })
      .then(res => setData(res.data))
  }, [page, size])

  return (
    <div className="p-4 bg-white rounded-lg shadow">
      <h2 className="text-lg font-semibold mb-2">Posts</h2>
      <div className="overflow-x-auto">
        <table className="min-w-full text-sm">
          <thead>
            <tr className="text-left border-b">
              <th className="p-2">Author</th>
              <th className="p-2">Content</th>
              <th className="p-2">Timestamp</th>
              <th className="p-2">Language</th>
              <th className="p-2">Sentiment</th>
            </tr>
          </thead>
          <tbody>
            {data?.content?.map((p) => (
              <tr key={p.id} className="border-b align-top">
                <td className="p-2">{p.author || '-'}</td>
                <td className="p-2 max-w-xl whitespace-pre-wrap">{p.content}</td>
                <td className="p-2">{p.timestamp}</td>
                <td className="p-2">{p.language}</td>
                <td className="p-2">{p.sentiment}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="flex items-center gap-2 mt-3">
        <button disabled={page === 0} onClick={() => setPage(p => Math.max(0, p - 1))} className="px-3 py-1 border rounded">Prev</button>
        <span>Page {page + 1} / {data?.totalPages ?? 1}</span>
        <button disabled={page + 1 >= (data?.totalPages ?? 1)} onClick={() => setPage(p => p + 1)} className="px-3 py-1 border rounded">Next</button>
      </div>
    </div>
  )
}