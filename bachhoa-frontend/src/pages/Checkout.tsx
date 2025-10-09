// src/pages/Checkout.tsx
import { useAppSelector, useAppDispatch } from '../app/hooks';
import { orderApi } from '../api/orderApi';
import { paymentApi } from '../api/paymentApi';
import { clear } from '../features/cart/cartSlice';
import { useNavigate } from 'react-router-dom';

export default function Checkout() {
  const items = useAppSelector((s) => s.cart.items);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const createOrder = async () => {
    const payload = { items: items.map((i) => ({ productId: i.id, quantity: i.qty })) };
    const order = await orderApi.create(payload);
    // create payment (stripe)
    try {
      const res = await paymentApi.createStripeIntent(order.id);
      dispatch(clear());
      navigate('/orders');
    } catch (e) {
      console.error(e);
      alert('Payment creation failed');
    }
  };

  return (
    <div>
      <h1 className="text-2xl font-semibold mb-4">Checkout</h1>
      <div className="bg-white p-4 rounded shadow">
        <div className="mb-3">Items: {items.length}</div>
        <button onClick={createOrder} className="bg-blue-600 text-white px-4 py-2 rounded">Place Order & Pay</button>
      </div>
    </div>
  );
}
