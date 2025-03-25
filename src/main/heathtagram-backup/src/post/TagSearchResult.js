import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";
import Sidebar from "../post/Sidebar";
import axios from "axios";
import styles from "../css/PostFeed.module.css"; // CSS 모듈 임포트

const TagSearchResult = () => {
  const { tagName } = useParams();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [lastPostId, setLastPostId] = useState(100000000000000000); // 초기 마지막 게시물 ID
  const [hasMore, setHasMore] = useState(true); // 더 이상 게시물이 없는지 여부
  const observerRef = useRef(null); // 무한 스크롤 감지용 ref
  const navigate = useNavigate(); // for navigation

  useEffect(() => {
    const token = localStorage.getItem("jwt_token");
    setIsAuthenticated(!!token);
    fetchTagSearchResults();
  }, []);

  const fetchTagSearchResults = async () => {
    if (loading || !hasMore) return;
    setLoading(true);
    setErrorMessage(null);

    try {
      const storedNickname = localStorage.getItem("nickname");
      const response = await axios.get(`http://localhost:8080/api/tagSearchResult`, {
        params: { tagSearching: tagName, lastPostId, storedNickname },
        headers: { Authorization: localStorage.getItem("jwt_token") },
        withCredentials: true,
      });

      const newPosts = response.data;
      if (newPosts.length === 0) {
        setHasMore(false);
      }
      if(newPosts.length > 0) {
        setPosts((prevPosts) => [...prevPosts, ...newPosts]);
        setLastPostId(newPosts[newPosts.length - 1].postId);
      }
    } catch (error) {
      setErrorMessage("게시물을 가져오는 데 실패했습니다.");
      console.error(error);
    }
    setLoading(false);
  };

  const handleLogout = () => {
    localStorage.removeItem("jwt_token");
    setIsAuthenticated(false);
    navigate("/signin");
  };

  const toggleLike = async (postId, likeState) => {
    const token = localStorage.getItem("jwt_token");
    const storedNickname = localStorage.getItem("nickname");

    try {
      const url = likeState
        ? `http://localhost:8080/api/cancelLikePost/${postId}`
        : `http://localhost:8080/api/likePost/${postId}`;

      await axios.put(url, null, {
        headers: { Authorization: token },
        params: { storedNickname },
      });

      setPosts((prevPosts) =>
        prevPosts.map((post) =>
          post.postId === postId
            ? {
                ...post,
                likeState: !likeState,
                heartCount: likeState ? post.heartCount - 1 : post.heartCount + 1
              }
            : post
        )
      );
    } catch (error) {
      console.error("좋아요 처리 실패", error);
    }
  };

  useEffect(() => {
    if (observerRef.current) {
      observerRef.current.disconnect();
    }

    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !loading) {
          fetchTagSearchResults();
        }
      },
      { threshold: 1 }
    );

    const triggerElement = document.getElementById("scroll-trigger");
    if (triggerElement) {
      observerRef.current.observe(triggerElement);
    }

    return () => observerRef.current?.disconnect();
  }, [hasMore, loading]);

  return (
    <div className={styles.tagSearchResultContainer}>
      <div className={styles.postFeed}>
        <Sidebar isAuthenticated={isAuthenticated} />
        <div className={styles.postsContainer}>
          <h1>#{tagName} 태그 검색 결과</h1>
          {posts.length === 0 ? (
            <p>검색된 게시물이 없습니다.</p>
          ) : (
            <div className={styles.postList}>
              {posts.map((post) => (
                <div key={post.postId} className={styles.postContainer}>
                  <div className={styles.postHeader}>
                    <span className={styles.postNickname}>{post.nickname}</span>
                    <span className={styles.postTime}>{new Date(post.createdAt).toLocaleString()}</span>
                  </div>
                  {post.filePath.length > 0 && <ImageSlider images={post.filePath} />}
                  <div className={styles.postLikeSection}>
                    <button
                      className={styles.postLikeButton}
                      onClick={() => toggleLike(post.postId, post.likeState)}
                    >
                      {post.likeState ? "❤️" : "🤍"}
                    </button>
                    <span className={styles.postLikes}>{post.heartCount}</span>
                  </div>
                  <div className={styles.postContent}>{post.content}</div>
                  {post.tagList && post.tagList.trim().length > 0 && (
                    <div className={styles.postTags}>
                      {post.tagList.split(",").map((tag, index) => (
                        <span key={index} className={styles.postTag}>#{tag}</span>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}

          <div id="scroll-trigger" style={{ height: "10px" }}></div>

          {loading && <p>로딩 중...</p>}
          {errorMessage && <p className="error-message">{errorMessage}</p>}
        </div>
      </div>
    </div>
  );
};

const ImageSlider = ({ images }) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  if (!images || !Array.isArray(images) || images.length === 0) {
    return null;
  }

  const nextImage = () => {
    if (currentIndex < images.length - 1) {
      setCurrentIndex((prevIndex) => prevIndex + 1);
    }
  };

  const prevImage = () => {
    if (currentIndex > 0) {
      setCurrentIndex((prevIndex) => prevIndex - 1);
    }
  };

  return (
    <div className={styles.imageSlider}>
      <img src={images[currentIndex]} alt="Post" className={styles.postImage} />
      {images.length > 1 && (
        <div className={styles.sliderControls}>
          {currentIndex > 0 && <button className={styles.prevButton} onClick={prevImage}>◀</button>}
          {currentIndex < images.length - 1 && <button className={styles.nextButton} onClick={nextImage}>▶</button>}
        </div>
      )}
    </div>
  );
};

export default TagSearchResult;
