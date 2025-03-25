import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom"; // 태그 검색 결과 페이지로 이동
import "../css/TagSearch.css";

const TagSearch = () => {
  const [tagSearch, setTagSearch] = useState("");  // ✅ 닉네임 검색과 변수 충돌 방지
  const [searchResults, setSearchResults] = useState([]); // 검색된 태그 목록
  const [totalResults, setTotalResults] = useState(0); // 총 검색된 태그 수
  const [loading, setLoading] = useState(false); // 로딩 상태
  const [errorMessage, setErrorMessage] = useState(null); // 에러 메시지
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지
  const [displayedResults, setDisplayedResults] = useState([]); // 화면에 표시된 태그
  const navigate = useNavigate(); // 페이지 이동을 위한 hook

  // ✅ 태그 검색 API 호출
  const handleSearch = async (page = 1) => {
    if (!tagSearch.trim()) {
      setSearchResults([]); // 검색어가 없으면 결과를 비운다
      setDisplayedResults([]); // 화면에 표시된 결과도 비운다
      return;
    }

    setLoading(true);
    setErrorMessage(null);

    try {
      const response = await axios.get(`http://localhost:8080/api/tagSearching?search=${tagSearch}&page=${page - 1}`);
      console.log("API 응답 데이터:", response.data);

      const tags = response.data;

      // 중복된 태그 제거 (Set을 사용하여 고유값만 유지)
      const uniqueTags = [...new Set(tags)];

      setSearchResults(uniqueTags);
      setTotalResults(uniqueTags.length);
      setCurrentPage(page);
      setLoading(false);
    } catch (error) {
      setErrorMessage("검색에 실패했습니다.");
      setLoading(false);
      console.error(error);
    }
  };

  // ✅ "더 보기" 버튼 클릭 시 추가 태그 표시
  const loadMoreResults = () => {
    const nextPage = currentPage + 1;
    const nextResults = searchResults.slice(currentPage * 5, (currentPage + 1) * 5);
    setDisplayedResults((prevResults) => [...prevResults, ...nextResults]);
    setCurrentPage(nextPage);
  };

  // ✅ 검색어 입력 처리
  const handleInputChange = (e) => {
    setTagSearch(e.target.value);
  };

  // ✅ 검색 제출 처리
  const handleSubmit = (e) => {
    e.preventDefault();
    setCurrentPage(1); // 검색 시 페이지 초기화
    setDisplayedResults([]); // 기존 검색 결과 초기화
    handleSearch();
  };

  // ✅ 태그 클릭 시 해당 태그 게시글 목록으로 이동
  const handleTagClick = (tagName) => {
    navigate(`/tagSearchResult/${tagName}`);
  };

  useEffect(() => {
    if (tagSearch.trim()) {
      handleSearch(1);
    } else {
      // 검색어가 비어있으면 결과를 초기화
      setDisplayedResults([]);
      setSearchResults([]);
    }
  }, [tagSearch]);

  useEffect(() => {
    if (searchResults.length > 0) {
      const initialResults = searchResults.slice(0, 100);
      setDisplayedResults(initialResults);
    }
  }, [searchResults]);

  return (
    <div className="tag-search-container">
      <div className="tag-search-form">
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={tagSearch}
            onChange={handleInputChange}
            placeholder="태그 검색"
            className="tag-search-input"
          />
        </form>
      </div>

      {loading && <div>로딩 중...</div>}
      {errorMessage && <div>{errorMessage}</div>}

      <div className="tag-search-results">
        {displayedResults.length > 0 ? (
          <div className="tag-list">
            {displayedResults.map((tag, index) => (
              <div key={index} className="tag-item" onClick={() => handleTagClick(tag)}>
                #{tag} {/* 태그 앞에 #을 붙여서 표시 */}
              </div>
            ))}
          </div>
        ) : (
          <div>검색된 태그가 없습니다.</div>
        )}
      </div>

      {displayedResults.length < searchResults.length && (
        <div className="load-more-container">
          <button className="load-more-button" onClick={loadMoreResults}>
            더 보기
          </button>
        </div>
      )}
    </div>
  );
};

export default TagSearch;
