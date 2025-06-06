// 전역 변수
let token = localStorage.getItem('token') || '';

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    // 토큰이 있으면 로그인 상태 표시
    updateAuthStatus();
    
    // 이벤트 리스너 등록
    registerEventListeners();
});

// 인증 상태 업데이트
function updateAuthStatus() {
    const isLoggedIn = !!token;
    
    // 로그인 상태에 따라 UI 변경
    document.querySelectorAll('.requires-auth').forEach(el => {
        el.style.display = isLoggedIn ? 'block' : 'none';
    });
    
    document.querySelectorAll('.requires-no-auth').forEach(el => {
        el.style.display = isLoggedIn ? 'none' : 'block';
    });
}

// 이벤트 리스너 등록
function registerEventListeners() {
    // 인증 관련
    document.getElementById('loginForm')?.addEventListener('submit', handleLogin);
    document.getElementById('logoutBtn')?.addEventListener('click', handleLogout);
    
    // 회원 관련
    document.getElementById('signupForm')?.addEventListener('submit', handleSignup);
    document.getElementById('updateProfileForm')?.addEventListener('submit', handleUpdateProfile);
    
    // 게시물 관련
    document.getElementById('createPostForm')?.addEventListener('submit', handleCreatePost);
    document.getElementById('getPostForm')?.addEventListener('submit', handleGetPost);
    document.getElementById('searchPostForm')?.addEventListener('submit', handleSearchPost);
    document.getElementById('updatePostForm')?.addEventListener('submit', handleUpdatePost);
    document.getElementById('deletePostForm')?.addEventListener('submit', handleDeletePost);
    
    // 댓글 관련
    document.getElementById('createCommentForm')?.addEventListener('submit', handleCreateComment);
    document.getElementById('deleteCommentForm')?.addEventListener('submit', handleDeleteComment);
}

// API 요청 함수
async function apiRequest(url, method, data = null, requiresAuth = false) {
    try {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (requiresAuth && token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        
        const options = {
            method,
            headers
        };
        
        if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
            options.body = JSON.stringify(data);
        }
        
        const response = await fetch(url, options);
        const responseData = await response.json();
        
        if (!response.ok) {
            throw new Error(responseData.message || '요청 처리 중 오류가 발생했습니다.');
        }
        
        return responseData;
    } catch (error) {
        console.error('API 요청 오류:', error);
        throw error;
    }
}

// 응답 표시 함수
function showResponse(elementId, data, isSuccess = true) {
    const responseElement = document.getElementById(elementId);
    if (!responseElement) return;
    
    responseElement.textContent = JSON.stringify(data, null, 2);
    responseElement.classList.remove('success', 'error');
    responseElement.classList.add(isSuccess ? 'success' : 'error');
    responseElement.style.display = 'block';
}

// 인증 핸들러
async function handleLogin(e) {
    e.preventDefault();
    
    const loginId = document.getElementById('loginId').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const response = await apiRequest('/auth/login', 'POST', {
            loginId,
            password
        });
        
        // 토큰 저장
        if (response.data && response.data.token) {
            token = response.data.token;
            localStorage.setItem('token', token);
            updateAuthStatus();
        }
        
        showResponse('loginResponse', response);
    } catch (error) {
        showResponse('loginResponse', { error: error.message }, false);
    }
}

async function handleLogout(e) {
    e.preventDefault();
    
    try {
        if (!token) {
            throw new Error('로그인되어 있지 않습니다.');
        }
        
        const response = await apiRequest('/auth/logout', 'POST', null, true);
        
        // 토큰 제거
        token = '';
        localStorage.removeItem('token');
        updateAuthStatus();
        
        showResponse('logoutResponse', response);
    } catch (error) {
        showResponse('logoutResponse', { error: error.message }, false);
    }
}

// 회원 핸들러
async function handleSignup(e) {
    e.preventDefault();
    
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;
    const nickname = document.getElementById('signupNickname').value;
    
    try {
        const response = await apiRequest('/members/signup', 'POST', {
            email,
            password,
            nickname
        });
        
        showResponse('signupResponse', response);
    } catch (error) {
        showResponse('signupResponse', { error: error.message }, false);
    }
}

async function handleUpdateProfile(e) {
    e.preventDefault();
    
    const nickname = document.getElementById('updateNickname').value;
    const password = document.getElementById('updatePassword').value;
    
    try {
        if (!token) {
            throw new Error('로그인이 필요합니다.');
        }
        
        const data = { nickname };
        if (password) {
            data.password = password;
        }
        
        const response = await apiRequest('/members/update', 'PATCH', data, true);
        
        showResponse('updateProfileResponse', response);
    } catch (error) {
        showResponse('updateProfileResponse', { error: error.message }, false);
    }
}

// 게시물 핸들러
async function handleCreatePost(e) {
    e.preventDefault();
    
    const title = document.getElementById('postTitle').value;
    const content = document.getElementById('postContent').value;
    
    try {
        if (!token) {
            throw new Error('로그인이 필요합니다.');
        }
        
        const response = await apiRequest('/api/posts', 'POST', {
            title,
            content
        }, true);
        
        showResponse('createPostResponse', response);
    } catch (error) {
        showResponse('createPostResponse', { error: error.message }, false);
    }
}

async function handleGetPost(e) {
    e.preventDefault();
    
    const postId = document.getElementById('getPostId').value;
    
    try {
        const response = await apiRequest(`/api/posts/${postId}`, 'GET');
        
        showResponse('getPostResponse', response);
    } catch (error) {
        showResponse('getPostResponse', { error: error.message }, false);
    }
}

async function handleSearchPost(e) {
    e.preventDefault();
    
    const searchType = document.getElementById('searchType').value;
    const searchQuery = document.getElementById('searchQuery').value;
    
    try {
        let url = '';
        
        switch (searchType) {
            case 'title':
                url = `/api/posts/posts/title?title=${encodeURIComponent(searchQuery)}`;
                break;
            case 'content':
                url = `/api/posts/posts/content?content=${encodeURIComponent(searchQuery)}`;
                break;
            case 'tc':
                url = `/api/posts/posts/tc?title=${encodeURIComponent(searchQuery)}&content=${encodeURIComponent(searchQuery)}`;
                break;
            case 'author':
                url = `/api/posts/posts/author?nickname=${encodeURIComponent(searchQuery)}`;
                break;
            default:
                throw new Error('잘못된 검색 유형입니다.');
        }
        
        const response = await apiRequest(url, 'GET');
        
        showResponse('searchPostResponse', response);
    } catch (error) {
        showResponse('searchPostResponse', { error: error.message }, false);
    }
}

async function handleUpdatePost(e) {
    e.preventDefault();
    
    const postId = document.getElementById('updatePostId').value;
    const title = document.getElementById('updatePostTitle').value;
    const content = document.getElementById('updatePostContent').value;
    
    try {
        if (!token) {
            throw new Error('로그인이 필요합니다.');
        }
        
        const response = await apiRequest(`/api/posts/${postId}`, 'PUT', {
            title,
            content
        }, true);
        
        showResponse('updatePostResponse', response);
    } catch (error) {
        showResponse('updatePostResponse', { error: error.message }, false);
    }
}

async function handleDeletePost(e) {
    e.preventDefault();
    
    const postId = document.getElementById('deletePostId').value;
    
    try {
        if (!token) {
            throw new Error('로그인이 필요합니다.');
        }
        
        const response = await apiRequest(`/api/posts/${postId}`, 'DELETE', null, true);
        
        showResponse('deletePostResponse', response);
    } catch (error) {
        showResponse('deletePostResponse', { error: error.message }, false);
    }
}

// 댓글 핸들러
async function handleCreateComment(e) {
    e.preventDefault();
    
    const postId = document.getElementById('commentPostId').value;
    const content = document.getElementById('commentContent').value;
    
    try {
        if (!token) {
            throw new Error('로그인이 필요합니다.');
        }
        
        const response = await apiRequest(`/api/posts/${postId}/comments`, 'POST', {
            content
        }, true);
        
        showResponse('createCommentResponse', response);
    } catch (error) {
        showResponse('createCommentResponse', { error: error.message }, false);
    }
}

async function handleDeleteComment(e) {
    e.preventDefault();
    
    const postId = document.getElementById('deleteCommentPostId').value;
    const commentId = document.getElementById('deleteCommentId').value;
    
    try {
        if (!token) {
            throw new Error('로그인이 필요합니다.');
        }
        
        const response = await apiRequest(`/api/posts/${postId}/comments/${commentId}`, 'DELETE', null, true);
        
        showResponse('deleteCommentResponse', response);
    } catch (error) {
        showResponse('deleteCommentResponse', { error: error.message }, false);
    }
} 