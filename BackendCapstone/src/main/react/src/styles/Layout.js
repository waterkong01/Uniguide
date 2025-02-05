import styled from "styled-components";
import TopNavBar from "../component/TopNavBar";
import ChatModal from "../pages/chat/ChatModal";
import MobileTopNavBar from "../component/MobileTopNavBar";
import {useEffect} from "react";
import {useDispatch} from "react-redux";
import {fetchUserStatus} from "../function/fetchUserStatus";
import {useLocation} from "react-router-dom";



const Background = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
`;
const Header = styled.div`
  width: 100%;
  height: 100px;
  position: relative;
  z-index: 1000;
`;

const PC = styled.div`

  @media (max-width: 768px) {
    display: none;
  }
`;

const Mobile = styled.div`
  display: none;

  @media (max-width: 768px) {
    display: flex;
  }
`;


const Layout = () => {
  const pathName = useLocation();
  
  useEffect(() => {
    fetchUserStatus();
  }, [pathName]);
  
  
  
  return (
    <Background>
      <Header>
        <PC>
          <TopNavBar/>
        </PC>
        <Mobile>
          <MobileTopNavBar/>
        </Mobile>
      </Header>
      <ChatModal />
    </Background>
  );
};

export default Layout;
