import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import AppRoutes from './routes/AppRoutes';
import { useEffect } from 'react';
import { useAppDispatch } from './app/hooks';
import { fetchMe } from './features/auth/authSlice';
import { getAccessToken } from './utils/token';

export default function App() {
  const dispatch = useAppDispatch();

  // Logic giữ đăng nhập khi tải lại trang (thuộc về toàn cục)
  useEffect(() => {
    const token = getAccessToken();
    if (token) {
      dispatch(fetchMe());
    }
  }, [dispatch]);

  return (
    // Sử dụng Fragment để chứa các thành phần toàn cục
    <>
      {/* AppRoutes sẽ quyết định layout nào được hiển thị */}
      <AppRoutes />

      {/* ToastContainer là toàn cục, hiển thị trên tất cả các trang */}
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={true}
        closeOnClick
        pauseOnHover
        draggable
      />
    </>
  );
}

