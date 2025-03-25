import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import styles from "../css/Search.module.css"; // 모듈화된 CSS 파일을 import

const Search = () => {
  const [nicknameSearch, setNicknameSearch] = useState(""); // 검색된 닉네임 상태
  const [searchResults, setSearchResults] = useState([]); // 검색된 결과 상태
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const navigate = useNavigate();

  // 검색 API 호출
  const handleSearch = async () => {
    if (!nicknameSearch.trim()) {
      setSearchResults([]); // 검색어가 없으면 결과 초기화
      return;
    }

    setLoading(true);
    setErrorMessage(null);

    try {
      const storedNickname = localStorage.getItem("nickname");

      const response = await axios.get(`http://localhost:8080/api/nicknameSearching`, {
        params: {
          search: nicknameSearch,
          storedNickname: storedNickname,
        },
      });

      setSearchResults(response.data.searchDto);
      setLoading(false);
    } catch (error) {
      setErrorMessage("검색에 실패했습니다.");
      setLoading(false);
      console.error(error);
    }
  };

  // 검색어 입력 처리
  const handleInputChange = (e) => {
    setNicknameSearch(e.target.value);
  };

  // 검색어가 변경될 때마다 자동 검색 (1글자 입력 시 검색)
  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      handleSearch();
    }, 300); // 300ms 딜레이 (디바운스)

    return () => clearTimeout(delayDebounceFn); // 이전 요청 취소
  }, [nicknameSearch]);

  return (
    <div className={styles.searchContainer}>
      <div className={styles.searchForm}>
        <input
          type="text"
          value={nicknameSearch}
          onChange={handleInputChange}
          placeholder="유저 검색"
          className={styles.searchInput}
        />
      </div>

      {loading && <div>로딩 중...</div>}
      {errorMessage && <div>{errorMessage}</div>}

      <div className={styles.searchResults}>
        {searchResults.length > 0 ? (
          searchResults.map((user, index) => (
            <div
              key={index}
              className={styles.searchResultItem}
              onClick={() => navigate(`/profile/${user.nickname}`)}
            >
              <div className={styles.profileInfo}>
                <img
                  src={user.profilePicture || "/default-profile.png"}
                  alt="프로필"
                  className={styles.profilePicture}
                />
              </div>
              <div className={styles.nicknameSpan}>
                <span>{user.nickname}</span>
              </div>
            </div>
          ))
        ) : (
          <div>검색된 결과가 없습니다.</div>
        )}
      </div>
    </div>
  );
};

export default Search;
