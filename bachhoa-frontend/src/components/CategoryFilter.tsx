// src/components/CategoryFilter.tsx

export default function CategoryFilter({ categories = [], onChange }: { categories?: { id: string; name: string }[]; onChange?: (id: string) => void }) {
  return (
    <div className="bg-white p-3 rounded shadow">
      <h4 className="font-semibold mb-2">Categories</h4>
      <ul className="space-y-2">
        <li><button onClick={() => onChange?.('')} className="text-left w-full">All</button></li>
        {categories?.map((c) => (
          <li key={c.id}>
            <button onClick={() => onChange?.(c.id)} className="text-left w-full">{c.name}</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
