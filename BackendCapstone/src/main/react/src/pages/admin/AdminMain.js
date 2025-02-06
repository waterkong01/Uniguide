import { BackGround } from "../../styles/GlobalStyle";
import {useContext, useEffect, useRef, useState} from "react";
import UploaderComponent from "./UploaderComponent";
import {PermissionContext} from "../../context/admin/PermissionStore";

const AdminMain = () => {
	
	const { setPage } = useContext(PermissionContext);
	
	useEffect(() => {
		setPage("main");
	}, []);
	
	return (
		<BackGround>
			<UploaderComponent type="univ.csv"/>
			<UploaderComponent type="textboard.csv"/>
		</BackGround>
	);
};

export default AdminMain;
