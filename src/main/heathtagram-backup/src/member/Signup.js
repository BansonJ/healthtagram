import React, { useState } from 'react';
import axios from 'axios';
import '../css/Signup.css';
import { useNavigate } from "react-router-dom"; // useNavigate 추가

const Signup = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    checkPassword: '',
    nickname: '',
    name: '',
    profilePicture: null
  });

  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate(); // useNavigate 훅 사용

  // 입력 값 처리
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  // 파일 업로드 처리
  const handleFileChange = (e) => {
    const { files } = e.target;
    setFormData({
      ...formData,
      profilePicture: files[0]
    });
  };

  // 회원가입 처리
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.checkPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    const signupDto = new FormData();

    // JSON 데이터로 회원가입 정보를 추가
    signupDto.append('signupDto', new Blob([JSON.stringify({
      email: formData.email,
      password: formData.password,
      checkPassword: formData.checkPassword,
      nickname: formData.nickname,
      name: formData.name
    })], { type: 'application/json' }));

    // 프로필 사진이 있으면 FormData에 추가
    if (formData.profilePicture) {
      signupDto.append('multipartFile', formData.profilePicture);
    }

    try {
      // 회원가입 API 호출
      const response = await axios.post('http://localhost:8080/api/signup', signupDto, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      setSuccessMessage('회원가입 성공!');
      setError('');
      setFormData({
        email: '',
        password: '',
        checkPassword: '',
        nickname: '',
        name: '',
        profilePicture: null
      });
      navigate("/"); // 회원가입 후 홈 페이지로 리디렉션
    } catch (error) {
      setError('회원가입에 실패했습니다. 다시 시도해 주세요.');
      setSuccessMessage('');
    }
  };

  return (
    <div className="signup-container">
      <h2>회원가입</h2>
      {error && <p className="error">{error}</p>}
      {successMessage && <p className="success">{successMessage}</p>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="email">이메일</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleInputChange}
            required
            placeholder="이메일을 입력하세요"
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">비밀번호</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleInputChange}
            required
            placeholder="비밀번호를 입력하세요"
          />
        </div>

        <div className="form-group">
          <label htmlFor="checkPassword">비밀번호 확인</label>
          <input
            type="password"
            id="checkPassword"
            name="checkPassword"
            value={formData.checkPassword}
            onChange={handleInputChange}
            required
            placeholder="비밀번호를 다시 입력하세요"
          />
        </div>

        <div className="form-group">
          <label htmlFor="nickname">닉네임</label>
          <input
            type="text"
            id="nickname"
            name="nickname"
            value={formData.nickname}
            onChange={handleInputChange}
            required
            placeholder="닉네임을 입력하세요"
          />
        </div>

        <div className="form-group">
          <label htmlFor="name">이름</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            required
            placeholder="이름을 입력하세요"
          />
        </div>

        <div className="form-group">
          <label htmlFor="profilePicture">프로필 사진</label>
          <input
            type="file"
            id="profilePicture"
            name="profilePicture"
            onChange={handleFileChange}
            accept="image/*"
          />
        </div>

        <button type="submit">회원가입</button>
      </form>
    </div>
  );
};

export default Signup;
