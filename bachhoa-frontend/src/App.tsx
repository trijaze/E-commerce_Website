import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import AppRoutes from './routes/AppRoutes';
import { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from './app/hooks';
import { fetchMe, setInitialized } from './features/auth/authSlice';
import { getAccessToken } from './utils/token';
import { RootState } from './app/store';

export default function App() {
  const dispatch = useAppDispatch();
  const { isInitialized } = useAppSelector((state: RootState) => state.auth);

  useEffect(() => {
    const token = getAccessToken();

    // Chỉ gọi fetchMe khi có token
    if (token) {
      dispatch(fetchMe())
        .unwrap()
        .catch(() => {
          // Nếu token hết hạn hoặc fetch thất bại → xóa token và đánh dấu khởi tạo xong
          localStorage.removeItem('access_token');
          localStorage.removeItem('refresh_token');
          dispatch(setInitialized());
        });
    } else {
      // Không có token → đánh dấu đã khởi tạo xong
      dispatch(setInitialized());
    }
  }, [dispatch]);

  if (!isInitialized) {
    return (
      <div className="flex items-center justify-center h-screen">
        <p>Đang tải ứng dụng...</p>
      </div>
    );
  }

  return (
    <>
      <AppRoutes />
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
