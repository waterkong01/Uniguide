import {BackGround} from "../../../../styles/GlobalStyle";
import {useSelector} from "react-redux";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";


const MemberItemMain = () => {
	const role = useSelector(state => state.persistent.role)
	const [member, setMember] = useState({});
	const {id} = useParams();
	
	useEffect(() => {
	
	}, []);
	
	
	
	
	
	return(
		<BackGround>
		
		</BackGround>
	)
}