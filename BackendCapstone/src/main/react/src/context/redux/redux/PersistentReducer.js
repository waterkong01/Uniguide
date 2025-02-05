import { createSlice } from "@reduxjs/toolkit";

const initialState = {
	role: localStorage.getItem("role") || "",
};

const PersistentReducer = createSlice({
	name: "persistent",
	initialState,
	reducers: {
		setRole: (state, action) => {
			state.role = action.payload;
			localStorage.setItem("role", action.payload);
		},
		logout: (state) => {
			console.log("로그아웃 처리!!!!!!!!")
			state.accessToken = "";
			state.refreshToken = "";
			state.role = "";
			localStorage.removeItem("accessToken");
			localStorage.removeItem("refreshToken");
			localStorage.removeItem("role");
		},
	},
});

export const {setRole, logout } = PersistentReducer.actions;
export default PersistentReducer.reducer;
