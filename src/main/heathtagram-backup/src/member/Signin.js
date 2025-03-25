import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom"; // React Router DOM 사용

import "../css/Signin.css"; // 스타일을 위한 CSS 파일

const Signin = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState(null);
  const navigate = useNavigate(); // 페이지 이동을 위한 navigate 훅

  // 폼 제출 시 로그인 처리
  const handleSubmit = async (e) => {
    e.preventDefault();

    const loginDto = {
      email,
      password,
    };

    try {
      // 로그인 API 호출
      const response = await axios.post("http://localhost:8080/api/signin", loginDto, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true, // 자격 증명 포함 요청
      });

      console.log("응답:", response);
      console.log("Headers:", response.headers);

      // 로그인 성공 시 JWT 토큰과 닉네임 저장
      const token = response.headers["authorization"];
      const nickname = response.data; // 백엔드에서 닉네임 포함하여 응답한다고 가정

      if (token && nickname) {
        localStorage.setItem("jwt_token", token); // 토큰 저장
        localStorage.setItem("nickname", nickname); // 닉네임 저장
        alert("로그인 성공!");
        navigate("/"); // ✅ 로그인 후 홈으로 이동
      }
    } catch (error) {
      setErrorMessage("로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.");
      console.error(error);
    }
  };

  return (
    <div className="login-container">
      <h1>로그인</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label>이메일</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="이메일을 입력하세요"
            required
          />
        </div>

        <div>
          <label>비밀번호</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="비밀번호를 입력하세요"
            required
          />
        </div>

        {errorMessage && <div className="error-message">{errorMessage}</div>}

        <button type="submit">로그인</button>
      </form>
    </div>
  );
};

export default Signin;
