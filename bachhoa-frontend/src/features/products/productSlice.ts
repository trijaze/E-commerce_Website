import { createSlice, createAsyncThunk, PayloadAction } from "@reduxjs/toolkit";
import { productApi } from "../../api/productApi";
import type { ProductsState, Product } from "./productTypes";

const initialState: ProductsState = {
  items: [],
  loading: false,
  error: null,
};

// Lấy danh sách sản phẩm (có thể kèm query)
export const fetchProducts = createAsyncThunk<Product[], any>(
  "products/fetch",
  async (q, { rejectWithValue }) => {
    try {
      return await productApi.list(q);
    } catch (e: unknown) {
      if (e instanceof Error) {
        return rejectWithValue({ message: e.message });
      }
      return rejectWithValue({ message: "Unknown error" });
    }
  }
);

// Lấy chi tiết sản phẩm theo ID
export const fetchProductById = createAsyncThunk<Product, string>(
  "products/fetchById",
  async (id, { rejectWithValue }) => {
    try {
      return await productApi.getById(Number(id));
    } catch (e: unknown) {
      if (e instanceof Error) {
        return rejectWithValue({ message: e.message });
      }
      return rejectWithValue({ message: "Unknown error" });
    }
  }
);

const slice = createSlice({
  name: "products",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // ==========================
      // fetchProducts
      // ==========================
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action: PayloadAction<Product[]>) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchProducts.rejected, (state, action: PayloadAction<any>) => {
        state.loading = false;
        state.error = action.payload?.message ?? "Failed to fetch products";
      })

      // ==========================
      // fetchProductById
      // ==========================
      .addCase(fetchProductById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProductById.fulfilled, (state, action: PayloadAction<Product>) => {
        state.loading = false;
        const idx = state.items.findIndex((p) => p.productId === action.payload.productId);
        if (idx >= 0) {
          state.items[idx] = action.payload; // cập nhật nếu đã có
        } else {
          state.items.unshift(action.payload); // thêm mới nếu chưa có
        }
      })
      .addCase(fetchProductById.rejected, (state, action: PayloadAction<any>) => {
        state.loading = false;
        state.error = action.payload?.message ?? "Failed to fetch product detail";
      });
  },
});

export default slice.reducer;
