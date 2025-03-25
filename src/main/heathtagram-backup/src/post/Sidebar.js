// Sidebar.js
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "../css/Sidebar.css";

const Sidebar = ({ isAuthenticated }) => {
  const navigate = useNavigate();
  const handleAuthNavigation = (path) => {
    if (isAuthenticated) {
      navigate(path);
    } else {
      navigate("/signin");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("jwt_token");
    navigate("/signin");
  };

  return (
    <div className="sidebar">
      <Link to="/" className="nav-link">🏠 홈</Link>
      <button className="nav-link" onClick={() => handleAuthNavigation(`/profile/${localStorage.getItem("nickname")}`)}>👤 내 프로필</button>
      <button className="nav-link" onClick={() => handleAuthNavigation("/postForm")}>✍️ 글쓰기</button>
      <button className="search-button" onClick={() => navigate("/search")}>🔍 유저 검색</button>
      <button className="search-button" onClick={() => navigate("/tagSearch")}>🏷️ 태그 검색</button>

      {isAuthenticated ? (
        <button className="nav-link" onClick={handleLogout}>🚪 로그아웃</button>
      ) : (
        <>
          <button className="nav-link" onClick={() => navigate("/signin")}>🔑 로그인</button>
          <button className="nav-link" onClick={() => navigate("/signup")}>🔑 회원가입</button>
        </>
      )}
    </div>
  );
};

export default Sidebar;
