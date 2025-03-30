import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Search from './member/Search';  // 검색 화면 컴포넌트
import Signup from './member/Signup';
import Signin from './member/Signin';
import Profile from "./member/Profile"; // 프로필 컴포넌트
import PostFeed from './post/PostFeed'; // PostFeed 컴포넌트
import PostForm from './post/PostForm'; // PostForm 컴포넌트
import UserPosts from "./member/UserPosts";
import TagSearch from './post/TagSearch';  // 검색 화면 컴포넌트
import TagSearchResult from "./post/TagSearchResult";
import FollowersList from "./follow/FollowersList";
import FollowingList from "./follow/FollowingList";
import Reply from "./reply/Reply";
import Weather from "./reply/Weather";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<PostFeed />} /> {/* 기본 페이지: PostFeed */}

        <Route path="/profile/:nickname" element={<Profile />} /> {/* 유저 프로필 페이지 */}

        {/* 회원가입 페이지 */}
        <Route path="/signup" element={<Signup />} />

        {/* 회원가입 페이지 */}
        <Route path="/postForm" element={<PostForm />} />

        {/* 로그인 페이지 */}
        <Route path="/signin" element={<Signin />} />

        <Route path="/post/:postId/reply" element={<Reply />} />

        <Route path="/memberPage/:nickname/post" element={<UserPosts />} />

        <Route path="/tagSearchResult/:tagName" element={<TagSearchResult />} />

        <Route path="/follower/:nickname" element={<FollowersList />} />

        <Route path="/following/:nickname" element={<FollowingList />} />

        {/* 검색 화면 */}
        <Route path="/search" element={<Search />} />

        <Route path="/tagSearch" element={<TagSearch />} />

        <Route path="/weather" element={<Weather />} />
      </Routes>
    </Router>
  );
}

export default App;
