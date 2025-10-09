import { createSlice, createAsyncThunk, PayloadAction } from "@reduxjs/toolkit";
import { productApi } from "../../api/productApi";
import type { ProductsState, Product } from "./productTypes";

const initialState: ProductsState = {
  items: [],
  loading: false,
  error: null,
};

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

export const fetchProductById = createAsyncThunk<Product, string>(
  "products/fetchById",
  async (id, { rejectWithValue }) => {
    try {
      return await productApi.getById(id);
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
      // fetchProducts
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
        state.error = action.payload?.message ?? "Failed";
      })

      // fetchProductById
      .addCase(fetchProductById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProductById.fulfilled, (state, action: PayloadAction<Product>) => {
        state.loading = false;
        const idx = state.items.findIndex((p) => p.id === action.payload.id);
        if (idx >= 0) state.items[idx] = action.payload;
        else state.items.unshift(action.payload);
      })
      .addCase(fetchProductById.rejected, (state, action: PayloadAction<any>) => {
        state.loading = false;
        state.error = action.payload?.message ?? "Failed";
      });
  },
});

export default slice.reducer;
