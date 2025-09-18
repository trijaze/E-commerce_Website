// src/pages/Cart.tsx
import { useAppSelector, useAppDispatch } from '../app/hooks';
import { remove, updateQty, clear } from '../features/cart/cartSlice';
import { Link, useNavigate } from 'react-router-dom';
import { formatCurrency } from '../utils/format';

export default function Cart() {
  const items = useAppSelector((s) => s.cart.items);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const total = items.reduce((a, b) => a + b.price * b.qty, 0);

  return (
    <div>
      <h1 className="text-2xl font-semibold mb-4">Shopping Cart</h1>
      {items.length === 0 ? (
        <div>
          Cart is empty. <Link to="/products" className="text-blue-600">Browse products</Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="md:col-span-2 space-y-3">
            {items.map((it) => (
              <div key={it.id} className="bg-white p-3 rounded shadow flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <img src={it.image ?? '/placeholder.jpg'} alt={it.name} className="w-20 h-20 object-cover rounded" />
                  <div>
                    <div className="font-semibold">{it.name}</div>
                    <div className="text-sm text-gray-500">{formatCurrency(it.price)}</div>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <input type="number" min={1} value={it.qty} onChange={(e) => dispatch(updateQty({ id: it.id, qty: Number(e.target.value) }))} className="w-16 border rounded px-2 py-1" />
                  <button onClick={() => dispatch(remove(it.id))} className="text-red-500">Remove</button>
                </div>
              </div>
            ))}
            <button onClick={() => dispatch(clear())} className="text-sm text-red-600">Clear cart</button>
          </div>
          <aside className="bg-white p-4 rounded shadow">
            <div className="text-lg">Subtotal: {formatCurrency(total)}</div>
            <button onClick={() => navigate('/checkout')} className="mt-4 w-full bg-blue-600 text-white py-2 rounded">Checkout</button>
          </aside>
        </div>
      )}
    </div>
  );
}
