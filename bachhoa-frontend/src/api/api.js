import axios from 'axios';

// Lấy URL từ biến môi trường, fallback về localhost nếu chưa có
const API = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
});

// Các hàm gọi API
export const getProducts = () => API.get('/products');
export const getCategories = () => API.get('/categories');
export const getCustomers = () => API.get('/customers');
export const getPromotions = () => API.get('/promotions');
export const loginAdmin = (username, password) =>
  API.post('/auth/login', { username, password });
export default API;
