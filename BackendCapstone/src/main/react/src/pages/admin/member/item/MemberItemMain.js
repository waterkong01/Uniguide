import {BackGround} from "../../../../styles/GlobalStyle";
import {useSelector} from "react-redux";
import {useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import AdminApi from "../../../../api/AdminApi";
import RejectModal from "../../../../component/RejectModal";
import ConfirmModal from "../../../../component/ConfirmModal";
import {PermissionContext} from "../../../../context/admin/PermissionStore";




const MemberItemMain = () => {
	const role = useSelector(state => state.persistent.role)
	const {member, setMember} = useContext(PermissionContext);
	const {id} = useParams();
	const [reject, setReject] = useState({});
	const [confirm, setConfirm] = useState({});
	const navigate = useNavigate();
	
	const fetchMember = async () => {
		try{
			const rsp = await AdminApi.getMemberDetails(id);
			console.log(rsp)
			if(!rsp.data) {
				setReject("멤버 정보가 불러와지지 않았습니다.")
			}
			setMember(rsp.data)
		} catch (error) {
			console.error(error)
			setReject("멤버 정보가 불러와지지 않았습니다.")
		}
	}
	
	useEffect(() => {
		fetchMember();
	}, [id]);
	
	const onChangeGeneral = (id, value) => {
		setMember({...member, [id]: value});
	}
	
	const onClickSubmit = async () => {
		{
			const rsp = await AdminApi.updateMember(id);
			console.log(rsp)
			if(rsp.data) {
				setConfirm({value : true, label: "수정에 성공 했습니다.", onConfirm: () => setConfirm({}), onCancle: () => navigate("/admin/member") })
				return
			}
			setReject("수정에 실패 했습니다.")
		}
	}
	
	const onClickCancel = () => {
		fetchMember()
	}
	
	
	
	return(
		<BackGround>
			
			<ConfirmModal onConfirm={confirm.onConfirm} message={confirm.label} onCancel={confirm.onCancel} open={confirm.value} />
			<RejectModal open={reject} onClose={() => setReject({})} message={reject}/>
		</BackGround>
	)
}