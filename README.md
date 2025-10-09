

## ⚙️ Yêu cầu
- Node.js >= 20
- npm > 10

---

## 🚀 Cách chạy project

1. Clone repo:
   ```bash
   git clone <URL_REPO>
   cd <TEN_THU_MUC_PROJECT>
   ```

2. Cài dependencies:

   ```bash
   npm install
   ```

3. Chạy môi trường phát triển:

   ```bash
   npm run dev
   ```

   👉 Mở [http://localhost:5173](http://localhost:5173)

---

## 🏗️ Build production

```bash
npm run build
```

Kết quả sẽ nằm trong thư mục `dist/`.

Chạy thử build:

```bash
npm run preview
```



## 📦 Công nghệ chính

* React + Vite
* Tailwind CSS
* Redux Toolkit
* React Router DOM
* React Toastify

## Các thư viện cần cài

* cài thư viện react-redux và kiểu @types/react-redux

```bash
npm install react-redux
npm install --save-dev @types/react-redux
```

*  thư viện @reduxjs/toolkit
```bash
npm install @reduxjs/toolkit

```

## Giao diện Sản Phẩm
<img width="1755" height="2329" alt="image" src="https://github.com/user-attachments/assets/fa1b90dd-2da4-48c9-b3d0-bd31c2905994" />






## Giao diện Review 


<img width="1755" height="2475" alt="image" src="https://github.com/user-attachments/assets/15a983f0-8613-43f7-9525-e426cf4a3644" />



## Giao diện User Profile +> Do chưa xây đăng ký và đăng nhập nên đã import mẫu sẵn thông tin ở authSlice.tsx bằng code 
```bash
const initialState: AuthState = {
  user: {
    id: '1',
    name: 'Trịnh Trần Phương Tuấn',
    email: 'ttpt@test.com',
    role: 'USER',
    createdAt: '2024-10-09T00:00:00Z'
  },
  loading: false,
  error: null
};

```
<img width="1755" height="1180" alt="image" src="https://github.com/user-attachments/assets/c75c7548-0498-4d05-a142-f34da8edfcfb" />




