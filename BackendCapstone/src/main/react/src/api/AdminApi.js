import Commons from "../util/Common";
import axiosInstance from "./AxiosInstance";

const baseUrl = ""
const token = Commons.getAccessToken()

// 주는거
const AdminApi = {
	getPermissionList: (category) => {
		console.log(`권한 부여 목록 호출 : ${category}`);
		return axiosInstance.get(baseUrl + `/admin/permission/list/${category}`);
	},
	getPermissionDetails: (permissionId) => {
		console.log(`권한 부여 세부 사항 호출 : ${permissionId}`);
		return axiosInstance.get(baseUrl + `/admin/permission/details/${permissionId}`);
	},
	activePermission: (permissionId, univId, isUniv) => {
		console.log(`권한 부여 : /admin/permission/active/${permissionId}/${univId}/${isUniv}`);
		return axiosInstance.post(baseUrl + `/admin/permission/active/${permissionId}/${univId}/${isUniv}`);
	},
	getUnivList: () => {
		console.log("대학 목록 불러오기(대학만)");
		return axiosInstance.get(baseUrl + `/admin/univ/list`);
	},
	getDeptList: (univName) => {
		console.log(`${univName}의 학과 조회`);
		return axiosInstance.get(baseUrl + `/admin/dept/list/${univName}`)
	},
	getMemberList: (searchOption, searchValue) => {
		console.log(`${searchOption} 전체 회원 조회 : ${searchValue}`);
		return axiosInstance.get(baseUrl + `/admin/member/${searchValue}/${searchOption}`);
	},
	getMemberDetails: (memberId) => {
		console.log(`${memberId}의 회원 세부 내용 확인`)
		return axiosInstance.get(baseUrl + `/admin/member/details/${memberId}`);
	},
	editMember(memberId, univId, authority, withdrawal) {
		console.log(`${memberId}의 정보 변경 : ${univId}-${authority}-${withdrawal}`);
		const member ={
			memberId: memberId,
			authority: authority,
			withdrawal: withdrawal,
			univId: univId,
		}
		return axiosInstance.post(baseUrl + `/admin/member/edit`, member)
	},
	uploadCsv: (file, type) => {
		switch (type) {
			case 'univ.csv':
				return axiosInstance.post(baseUrl + `/admin/csv/univ`, file);
			case 'textboard.csv':
				return axiosInstance.post(baseUrl + `/admin/csv/textBoard`, file);
			default:
				return null
		}
	}
}
export default AdminApi;