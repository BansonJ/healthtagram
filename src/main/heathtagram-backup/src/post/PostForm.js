import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../css/PostForm.css";

const PostForm = () => {
  const [content, setContent] = useState("");
  const [images, setImages] = useState([]);
  const [tags, setTags] = useState([]); // ✅ 태그 리스트 (공백 없이 저장)
  const [tagInput, setTagInput] = useState("");
  const navigate = useNavigate();

  // ✅ 이미지 선택 핸들러
  const handleImageChange = (event) => {
    const files = Array.from(event.target.files);
    setImages((prevImages) => [...prevImages, ...files]);
  };

  // ✅ 이미지 삭제 핸들러
  const handleImageRemove = (index) => {
    setImages(images.filter((_, i) => i !== index));
  };

  // ✅ 태그 추가 핸들러 (공백 제거 & 중복 방지)
  const handleTagAdd = () => {
    const newTags = tagInput.trim().split(/[ ,]+/).filter((tag) => tag.length > 0); // 공백 & 쉼표로 분리
    const uniqueTags = [...new Set([...tags, ...newTags])]; // 중복 제거

    setTags(uniqueTags);
    setTagInput(""); // 입력창 초기화
  };

  // ✅ 태그 삭제 핸들러
  const handleTagRemove = (tagToRemove) => {
    setTags(tags.filter((tag) => tag !== tagToRemove));
  };

  // ✅ 게시글 제출
  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!content.trim() && images.length === 0) {
      alert("내용 또는 이미지를 추가해주세요!");
      return;
    }

    const formData = new FormData();
    images.forEach((image) => formData.append("multipartFile", image));
    formData.append(
      "postRequestDto",
      new Blob(
        [
          JSON.stringify({
            content: content,
            tagList: tags.join(","), // ✅ 태그를 쉼표(`,`)로 구분하여 String으로 변환
          }),
        ],
        { type: "application/json" }
      )
    );

    // Get the nickname from localStorage
    const storedNickname = localStorage.getItem("nickname");

    try {
      const token = localStorage.getItem("jwt_token");

      // Send nickname as a query parameter (RequestParam)
      await axios.post(
        "http://localhost:8080/api/post",
        formData,
        {
          headers: {
            Authorization: `${token}`,
            "Content-Type": "multipart/form-data",
          },
          params: { storedNickname: storedNickname }, // 보내는 nickname을 query parameter로 포함
        }
      );

      alert("게시물이 업로드되었습니다!");
      navigate("/"); // ✅ 게시물 업로드 후 홈으로 이동
    } catch (error) {
      console.error("게시물 업로드 실패", error);
      alert("게시물 업로드 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="post-form-container">
      <h2>새 게시물 만들기</h2>
      <form onSubmit={handleSubmit} className="post-form">
        {/* 이미지 업로드 */}
        <label className="image-upload-label">
          이미지 선택
          <input type="file" multiple accept="image/*" onChange={handleImageChange} />
        </label>

        {/* 선택한 이미지 미리보기 */}
        <div className="image-preview">
          {images.map((image, index) => (
            <div key={index} className="image-item">
              <img src={URL.createObjectURL(image)} alt={`preview-${index}`} />
              <span className="image-filename">{image.name}</span> {/* 파일 이름 표시 */}
              <button type="button" className="image-remove-btn" onClick={() => handleImageRemove(index)}>✖</button>
            </div>
          ))}
        </div>

        {/* 게시글 내용 입력 */}
        <textarea
          placeholder="문구를 입력하세요..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
          className="post-content-input"
        />

        {/* 태그 입력 */}
        <div className="tag-input-container">
          <input
            type="text"
            placeholder="태그 입력 후 Enter"
            value={tagInput}
            onChange={(e) => setTagInput(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === "Enter") {
                handleTagAdd();
                e.preventDefault(); // 기본 Enter 기능 방지
              }
            }}
          />
          <button type="button" onClick={handleTagAdd}>
            추가
          </button>
        </div>

        {/* 태그 목록 */}
        <div className="tag-list">
          {tags.map((tag, index) => (
            <span key={index} className="tag">
              #{tag}
              <button type="button" onClick={() => handleTagRemove(tag)}>✖</button>
            </span>
          ))}
        </div>

        {/* 게시 버튼 */}
        <button type="submit" className="post-submit-button">게시</button>
      </form>
    </div>
  );
};

export default PostForm;
