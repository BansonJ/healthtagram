import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import styles from '../css/Reply.module.css';

const Reply = () => {
  const { postId } = useParams();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [lastReplyId, setLastReplyId] = useState(null);
  const commentSectionRef = useRef(null);

  useEffect(() => {
    const fetchPostAndComments = async () => {
      try {
        const storedNickname = localStorage.getItem('nickname');
        const initialLastReplyId = Number.MAX_SAFE_LONG;

        // 댓글 정보와 게시물 정보 가져오기
        const response = await axios.get(`http://localhost:8080/api/${postId}/reply`, {
          params: {
            lastReplyId: initialLastReplyId,
            storedNickname: storedNickname,
          }
        });

        const { post: fetchedPost, profilePicture, replyMemberResponseDtoList } = response.data;

        setPost(fetchedPost);
        setComments(replyMemberResponseDtoList);
        setLastReplyId(replyMemberResponseDtoList[replyMemberResponseDtoList.length - 1].reply.id);
        setLoading(false);
      } catch (error) {
        console.error("댓글 정보를 불러오는 데 실패했습니다.", error);
        setLoading(false);
      }
    };

    if (postId) {
      fetchPostAndComments();
    }
  }, [postId]);

  const handleCommentChange = (e) => {
    setNewComment(e.target.value);
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    try {
      const storedNickname = localStorage.getItem('nickname');

      // 댓글 작성 API 호출
      const response = await axios.post(
        `http://localhost:8080/api/${postId}/replyUp?storedNickname=${storedNickname}`,
        newComment  // 이미 문자열로 보내도록 수정
      );

      const newCommentData = response.data;

      const updatedComment = {
        reply: newCommentData.reply,
        profilePicture: newCommentData.profilePicture,
        state: false  // 새 댓글은 기본적으로 좋아요 상태가 아님
      };

      setComments((prevComments) => [updatedComment, ...prevComments]);
      setNewComment('');
    } catch (error) {
      console.error("댓글 작성 실패", error);
    }
  };

  const handleLoadMoreComments = async () => {
    try {
      const storedNickname = localStorage.getItem('nickname');

      const response = await axios.get(`http://localhost:8080/api/${postId}/reply`, {
        params: {
          lastReplyId: lastReplyId,
          storedNickname: storedNickname,
        }
      });

      const { replyMemberResponseDtoList } = response.data;

      setComments((prevComments) => [...prevComments, ...replyMemberResponseDtoList]);
      setLastReplyId(replyMemberResponseDtoList[replyMemberResponseDtoList.length - 1].reply.id);
    } catch (error) {
      console.error("댓글 추가 불러오기 실패", error);
    }
  };

  const handleLikeReply = async (replyId) => {
    try {
      const storedNickname = localStorage.getItem('nickname');
      await axios.put(`http://localhost:8080/api/${postId}/likeReply/${replyId}`, null, {
        params: { storedNickname }
      });

      // 좋아요 상태를 반영하여 댓글 목록 업데이트
      setComments((prevComments) => prevComments.map((comment) =>
        comment.reply.id === replyId
          ? { ...comment, state: true }  // 좋아요 상태로 업데이트
          : comment
      ));
    } catch (error) {
      console.error("댓글 좋아요 실패", error);
    }
  };

  const handleCancelLikeReply = async (replyId) => {
    try {
      const storedNickname = localStorage.getItem('nickname');
      await axios.delete(`http://localhost:8080/api/${postId}/cancelLikeReply/${replyId}`, {
        params: { storedNickname }
      });

      // 좋아요 취소 후 댓글 목록 업데이트
      setComments((prevComments) => prevComments.map((comment) =>
        comment.reply.id === replyId
          ? { ...comment, state: false }  // 좋아요 취소 상태로 업데이트
          : comment
      ));
    } catch (error) {
      console.error("댓글 좋아요 취소 실패", error);
    }
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className={styles.overlay}>
      <div className={styles.replyContainer}>
        <div className={styles.left}>
          {/* 게시물 이미지 슬라이드 추가 */}
          <ImageSlider images={post.filePath} />

          <div className={styles.postContent}>
            <div className={styles.nickname}>{post.nickname}</div>
            <div className={styles.content}>{post.content}</div>
          </div>
        </div>

        <div className={styles.right}>
          <div ref={commentSectionRef} className={styles.commentsSection}>
            <div className={styles.commentList}>
              {comments.length === 0 ? (
                <p className={styles.noComments}>댓글이 없습니다.</p>
              ) : (
                comments.map((comment, index) => (
                  <div className={styles.comment} key={index}>
                    <div className={styles.commentHeader}>
                      <img
                        src={comment.profilePicture || "/default-profile.png"}
                        alt="댓글 작성자"
                        className={styles.commentProfilePicture}
                      />
                      <div className={styles.commentNickname}>{comment.reply.nickname}</div>
                    </div>
                    {/* 댓글과 좋아요 버튼을 같은 줄에 배치 */}
                    <div className={styles.commentContentAndLike}>
                      <div className={styles.commentContent}>{comment.reply.reply}</div>
                      <div className={styles.likeButtonWrapper}>
                        {comment.state ? (
                          <button
                            onClick={() => handleCancelLikeReply(comment.reply.id)}
                            className={styles.likedButton}
                          >
                            ❤️
                          </button>
                        ) : (
                          <button
                            onClick={() => handleLikeReply(comment.reply.id)}
                            className={styles.unlikedButton}
                          >
                            🤍
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {comments.length >= 5 && (
            <div className={styles.loadMoreWrapper}>
              <button onClick={handleLoadMoreComments} className={styles.loadMoreButton}>
                + 더보기
              </button>
            </div>
          )}

          <form onSubmit={handleCommentSubmit} className={styles.commentForm}>
            <textarea
              value={newComment}
              onChange={handleCommentChange}
              placeholder="댓글을 입력하세요..."
              className={styles.commentInput}
            />
            <button type="submit" className={styles.commentSubmit}>게시</button>
          </form>
        </div>
      </div>
    </div>
  );
};

// 게시물 이미지 슬라이드 컴포넌트
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
      <img src={images[currentIndex]} alt="게시물 이미지" className={styles.postImage} />

      {images.length > 1 && (
        <div className={styles.sliderControls}>
          {currentIndex > 0 && <button className={styles.prevButton} onClick={prevImage}>◀</button>}
          {currentIndex < images.length - 1 && <button className={styles.nextButton} onClick={nextImage}>▶</button>}
        </div>
      )}
    </div>
  );
};

export default Reply;
