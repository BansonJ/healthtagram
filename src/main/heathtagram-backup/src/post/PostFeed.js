import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import styles from "../css/PostFeed.module.css"; // CSS 모듈 import

const PostFeed = () => {
  const [posts, setPosts] = useState([]);
  const [lastPostId, setLastPostId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const navigate = useNavigate();
  const observerRef = useRef(null);

  useEffect(() => {
    const token = localStorage.getItem("jwt_token");
    setIsAuthenticated(!!token);
    fetchPosts();
  }, []);

  const fetchPosts = async () => {
    if (loading || !hasMore) return;
    setLoading(true);

    const storedNickname = localStorage.getItem("nickname");

    try {
      const response = await axios.get("http://localhost:8080/api/home", {
        params: { lastPostId, storedNickname },
        headers: { Authorization: localStorage.getItem("jwt_token") },
        withCredentials: true,
      });

      const fetchedPosts = response.data;
      if (fetchedPosts.length === 0) setHasMore(false);
      setPosts((prevPosts) => [...prevPosts, ...fetchedPosts]);
      if (fetchedPosts.length > 0) setLastPostId(fetchedPosts[fetchedPosts.length - 1].postId);
    } catch (error) {
      console.error("게시물을 가져오는 데 실패했습니다.", error);
    }
    setLoading(false);
  };

  useEffect(() => {
    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !loading) {
          fetchPosts();
        }
      },
      { threshold: 1 }
    );

    if (observerRef.current && document.getElementById("scroll-trigger")) {
      observerRef.current.observe(document.getElementById("scroll-trigger"));
    }

    return () => observerRef.current?.disconnect();
  }, [hasMore, loading]);

  const handleAuthNavigation = (path) => {
    if (isAuthenticated) {
      navigate(path);
    } else {
      navigate("/signin");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("jwt_token");
    setIsAuthenticated(false);
    navigate("/signin");
  };

  const toggleLike = async (postId, likeState) => {
    try {
      const token = localStorage.getItem("jwt_token");
      const storedNickname = localStorage.getItem("nickname");
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
                heartCount: likeState ? post.heartCount - 1 : post.heartCount + 1,
              }
            : post
        )
      );
    } catch (error) {
      console.error("좋아요 처리 실패", error);
    }
  };

  const navigateToReplies = (postId) => {
    navigate(`/post/${postId}/reply`);
  };

  return (
    <div className={styles.postFeed}>
      <div className={styles.sidebar}>
        <Link to="/" className={styles.navLink}>🏠 홈</Link>
        <button className={styles.navLink} onClick={() => handleAuthNavigation(`/profile/${localStorage.getItem("nickname")}`)}>👤 내 프로필</button>
        <button className={styles.navLink} onClick={() => handleAuthNavigation("/postForm")}>✍️ 글쓰기</button>
        <button className={styles.navLink} onClick={() => navigate("/search")}>🔍 유저 검색</button>
        <button className={styles.navLink} onClick={() => navigate("/tagSearch")}>🏷️ 태그 검색</button>

        {isAuthenticated ? (
          <button className={styles.navLink} onClick={handleLogout}>🚪 로그아웃</button>
        ) : (
          <>
            <button className={styles.navLink} onClick={() => navigate("/signin")}>🔑 로그인</button>
            <button className={styles.navLink} onClick={() => navigate("/signup")}>🔑 회원가입</button>
          </>
        )}
      </div>

      <div className={styles.postsContainer}>
        {posts.length === 0 ? (
          <p>게시물이 없습니다.</p>
        ) : (
          posts.map((post) => (
            <div className={styles.postContainer} key={post.postId}>
              <div className={styles.postHeader}>
                <span className={styles.postNickname}>{post.nickname}</span>
                <span className={styles.postTime}>{new Date(post.createdAt).toLocaleString()}</span>
              </div>

              {post.filePath.length > 0 && <ImageSlider images={post.filePath} />}

              <div>
                <button className={styles.postLikeButton} onClick={() => toggleLike(post.postId, post.likeState)}>
                  {post.likeState ? "❤️" : "🤍"}
                </button>
                <span className={styles.postLikes}>{post.heartCount}</span>

                <button className={styles.postCommentButton} onClick={() => navigateToReplies(post.postId)}>
                  💬
                </button>
              </div>

              <div className={styles.postContent}>{post.content}</div>

              <div className={styles.postTags}>
                {post.tagList && post.tagList.trim().length > 0 && (
                  <div className={styles.tags}>
                    {post.tagList.split(",").map((tag, index) => (
                      <span key={index} className={styles.postTag}>
                        {tag.startsWith("#") ? tag : `#${tag}`}
                      </span>
                    ))}
                  </div>
                )}
              </div>
            </div>
          ))
        )}

        <div id="scroll-trigger" style={{ height: "10px" }}></div>
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

export default PostFeed;
