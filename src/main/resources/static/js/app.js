/*Index Start*/
// Online မှာ တိုက်ရိုက်ဖတ်နိုင်သော စာအုပ်များ Data
const featuredBooks = [
    {
        id: 1,
        title: "The Midnight Library",
        author: "Matt Haig",
        status: "Available",
        img: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400",
        readUrl: "#"
    },
    {
        id: 2,
        title: "Atomic Habits",
        author: "James Clear",
        status: "Available",
        img: "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400",
        readUrl: "#"
    },
    {
        id: 3,
        title: "Deep Work",
        author: "Cal Newport",
        status: "Unavailable",
        img: "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400",
        readUrl: "#"
    },
    {
        id: 4,
        title: "Educated",
        author: "Tara Westover",
        status: "Available",
        img: "https://images.unsplash.com/photo-1532012164546-f43249483d31?w=400",
        readUrl: "#"
    }
];

document.addEventListener("DOMContentLoaded", () => {
    renderFeaturedBooks();
    loadProfileData();

    // Profile View နဲ့ Edit Toggle ရေးသားချက်
    const toggleEditBtn = document.getElementById("toggleEditBtn");
    const cancelEditBtn = document.getElementById("cancelEditBtn");
    const profileViewArea = document.getElementById("profileViewArea");
    const profileForm = document.getElementById("profileForm");

    // Edit Profile နှိပ်လိုက်ပါက Form ပေါ်လာမည်
    toggleEditBtn.addEventListener("click", () => {
        profileViewArea.classList.add("d-none");
        profileForm.classList.remove("d-none");
    });

    // Cancel နှိပ်ပါက View အဟောင်းသို့ ပြန်သွားမည်
    cancelEditBtn.addEventListener("click", () => {
        profileForm.classList.add("d-none");
        profileViewArea.classList.remove("d-none");
    });

    // Save Changes နှိပ်ပါက LocalStorage ထဲ သိမ်းဆည်းမည်
    profileForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const profileData = {
            name: document.getElementById("inputName").value,
            phone: document.getElementById("inputPhone").value,
            bio: document.getElementById("inputBio").value,
            username: document.getElementById("inputUsername").value,
            birthday: document.getElementById("inputBirthday").value
        };

        localStorage.setItem("reader_user_profile", JSON.stringify(profileData));
        updateProfileUI(profileData);

        profileForm.classList.add("d-none");
        profileViewArea.classList.remove("d-none");
    });
});

// Book Card များကို HTML သို့ အလိုအလျောက် ထည့်သွင်းခြင်း
function renderFeaturedBooks() {
    const container = document.getElementById("featuredBooksContainer");
    if (!container) return;

    container.innerHTML = "";

    featuredBooks.forEach(book => {
        const isAvailable = book.status === "Available";
        const badgeBg = isAvailable ? "bg-success" : "bg-danger";

        const cardHTML = `
            <div class="col-lg-3 col-md-6">
                <div class="card h-100 book-card">
                    <img src="${book.img}" class="book-cover" alt="${book.title}">
                    <div class="card-body d-flex flex-column">
                        <span class="badge ${badgeBg} mb-2 align-self-start">${book.status}</span>
                        <h5 class="card-title text-white fw-bold fs-6 mb-1">${book.title}</h5>
                        <p class="text-light-50 small mb-3">By ${book.author}</p>
                        
                        <a href="${book.readUrl}" class="btn btn-warning fw-bold btn-sm mt-auto w-100 ${!isAvailable ? 'disabled' : ''}">
                            <i class="fa-solid fa-book-open me-1"></i> ${isAvailable ? 'Read Book' : 'Unavailable'}
                        </a>
                    </div>
                </div>
            </div>
        `;
        container.insertAdjacentHTML("beforeend", cardHTML);
    });
}

// LocalStorage ထဲမှ Profile အချက်အလက်များကို ပြန်လည်ရယူခြင်း
function loadProfileData() {
    const savedProfile = localStorage.getItem("reader_user_profile");
    if (savedProfile) {
        const profile = JSON.parse(savedProfile);
        updateProfileUI(profile);
    }
}

// UI တွင် Profile အချက်အလက်များ ပြသပေးခြင်း
function updateProfileUI(data) {
    document.getElementById("dispName").textContent = data.name || "Please add your name";
    document.getElementById("dispPhone").textContent = data.phone || "Please add your phone number";
    document.getElementById("dispBio").textContent = data.bio || "Please add your bio";
    document.getElementById("dispUsername").textContent = data.username ? `@${data.username}` : "@your_username";
    document.getElementById("dispBirthday").textContent = data.birthday || "Please add your birthday (e.g. Jan 13, 2005)";

    // Form Field ထဲတွင် အသင့်ဖြည့်ထားပေးခြင်း
    document.getElementById("inputName").value = data.name || "";
    document.getElementById("inputPhone").value = data.phone || "";
    document.getElementById("inputBio").value = data.bio || "";
    document.getElementById("inputUsername").value = data.username || "";
    document.getElementById("inputBirthday").value = data.birthday || "";
}
/*Index End*/

/*Catalog Start*/

/*Catalog End*/

/*History Start*/
// Sample Online Reading History Data
const readingLogs = [
    {
        bookId: "BK-1001",
        title: "The Midnight Library",
        author: "Matt Haig",
        lastAccessed: "Today, 10:30 AM",
        status: "In Progress",
        progress: "75%",
        statusBg: "bg-warning text-dark"
    },
    {
        bookId: "BK-1002",
        title: "Atomic Habits",
        author: "James Clear",
        lastAccessed: "Yesterday, 04:15 PM",
        status: "Completed",
        progress: "100%",
        statusBg: "bg-success text-white"
    },
    {
        bookId: "BK-1003",
        title: "Deep Work",
        author: "Cal Newport",
        lastAccessed: "Jul 20, 2026",
        status: "In Progress",
        progress: "40%",
        statusBg: "bg-warning text-dark"
    },
    {
        bookId: "BK-1004",
        title: "Educated",
        author: "Tara Westover",
        lastAccessed: "Jul 18, 2026",
        status: "Completed",
        progress: "100%",
        statusBg: "bg-success text-white"
    }
];

document.addEventListener("DOMContentLoaded", () => {
    renderLogsTable(readingLogs);
    loadProfileData();
    setupProfileModal();

    // Sync Online Notification
    const syncBtn = document.getElementById("syncBtn");
    if (syncBtn) {
        syncBtn.addEventListener("click", () => {
            alert("Online reading history synced successfully!");
        });
    }
});

// Render Logs into Page
function renderLogsTable(logs) {
    const container = document.getElementById("readingLogsContainer");
    if (!container) return;

    container.innerHTML = "";

    logs.forEach(item => {
        const rowHTML = `
            <div class="log-row-item d-flex flex-column flex-md-row align-items-md-center gap-2">
                <div class="col-md-2">
                    <span class="badge bg-light text-dark border fw-bold">${item.bookId}</span>
                </div>
                <div class="col-md-4">
                    <h6 class="fw-bold text-dark mb-0">${item.title}</h6>
                    <small class="text-secondary">By ${item.author}</small>
                </div>
                <div class="col-md-2 text-secondary small">
                    <i class="fa-regular fa-clock me-1"></i>${item.lastAccessed}
                </div>
                <div class="col-md-2">
                    <span class="badge ${item.statusBg} rounded-pill px-3 py-1">${item.status} (${item.progress})</span>
                </div>
                <div class="col-md-2 text-md-end mt-2 mt-md-0">
                    <a href="catalog.html" class="btn btn-outline-dark btn-sm rounded-pill px-3">
                        <i class="fa-solid fa-book-open me-1"></i> Read Online
                    </a>
                </div>
            </div>
        `;
        container.insertAdjacentHTML("beforeend", rowHTML);
    });
}

// Profile Modal Script
function loadProfileData() {
    const savedProfile = localStorage.getItem("reader_user_profile");
    if (savedProfile) {
        const profile = JSON.parse(savedProfile);
        document.getElementById("dispName").textContent = profile.name || "Please add name";
        document.getElementById("dispPhone").textContent = profile.phone || "Please add phone";
        document.getElementById("dispBio").textContent = profile.bio || "Please add bio";
        document.getElementById("dispUsername").textContent = profile.username ? `@${profile.username}` : "@username";
        document.getElementById("dispBirthday").textContent = profile.birthday || "Please add birthday";
    }
}

function setupProfileModal() {
    const toggleEditBtn = document.getElementById("toggleEditBtn");
    const cancelEditBtn = document.getElementById("cancelEditBtn");
    const profileViewArea = document.getElementById("profileViewArea");
    const profileForm = document.getElementById("profileForm");

    if (toggleEditBtn) {
        toggleEditBtn.addEventListener("click", () => {
            profileViewArea.classList.add("d-none");
            profileForm.classList.remove("d-none");
        });
    }

    if (cancelEditBtn) {
        cancelEditBtn.addEventListener("click", () => {
            profileForm.classList.add("d-none");
            profileViewArea.classList.remove("d-none");
        });
    }
}
/*History End*/

/*About Start*/

/*About End*/

/*Contact Start*/

/*Contact End*/

/*Overview Start*/
document.addEventListener('DOMContentLoaded', () => {
    // Search input interaction
    const searchInput = document.getElementById('searchInput');
    
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.toLowerCase();
            console.log('Searching for:', query);
            // Dynamic filtering logic can be added here
        });
    }

    // Logout Button Event
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            if (confirm('Are you sure you want to log out?')) {
                window.location.href = 'login.html'; // Redirect to login page
            }
        });
    }

    // Disconnect Portal Button Event
    const disconnectBtn = document.querySelector('.btn-disconnect');
    if (disconnectBtn) {
        disconnectBtn.addEventListener('click', () => {
            alert('Portal Session Disconnected.');
        });
    }
});
/*Overview End*/

/*Book Catalog Start*/
document.addEventListener('DOMContentLoaded', () => {
    // Modal Elements
    const addBookModal = document.getElementById('addBookModal');
    const openAddModalBtn = document.getElementById('openAddModalBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const addBookForm = document.getElementById('addBookForm');
    const booksTableBody = document.getElementById('booksTableBody');
    const totalBooksCount = document.getElementById('totalBooksCount');

    // 1. Open Modal
    if (openAddModalBtn) {
        openAddModalBtn.addEventListener('click', () => {
            addBookModal.classList.add('active');
        });
    }

    // 2. Close Modal Functions
    const closeModal = () => {
        addBookModal.classList.remove('active');
        addBookForm.reset();
    };

    if (closeModalBtn) closeModalBtn.addEventListener('click', closeModal);
    if (cancelBtn) cancelBtn.addEventListener('click', closeModal);

    // 3. Handle Add New Book Form Submission
    if (addBookForm) {
        addBookForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const title = document.getElementById('bookTitle').value;
            const author = document.getElementById('bookAuthor').value;
            const desc = document.getElementById('bookDesc').value;
            const coverInput = document.getElementById('bookCover');

            // Handle Image preview
            let coverHtml = `<div class="book-cover-placeholder"><i class="fa-solid fa-book-open"></i></div>`;
            
            if (coverInput.files && coverInput.files[0]) {
                const imgUrl = URL.createObjectURL(coverInput.files[0]);
                coverHtml = `<img src="${imgUrl}" class="uploaded-cover" alt="Cover">`;
            }

            // Create new Row
            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                <td>${coverHtml}</td>
                <td><strong class="text-bright">${title}</strong></td>
                <td class="text-secondary-bright">${author}</td>
                <td class="text-secondary-bright">${desc}</td>
                <td class="action-buttons">
                    <button class="btn-action view"><i class="fa-solid fa-eye"></i></button>
                    <button class="btn-action edit"><i class="fa-solid fa-pen"></i></button>
                    <button class="btn-action delete"><i class="fa-solid fa-trash"></i></button>
                </td>
            `;

            // Append to table
            booksTableBody.appendChild(newRow);

            // Update Metrics Count
            if (totalBooksCount) {
                let currentNum = parseInt(totalBooksCount.textContent.replace(',', '')) || 0;
                totalBooksCount.textContent = (currentNum + 1).toLocaleString();
            }

            closeModal();
            alert('New book added successfully!');
        });
    }

    // 4. Live Table Search Filter
    const searchInput = document.getElementById('catalogSearch');
    if (searchInput) {
        searchInput.addEventListener('keyup', () => {
            const filter = searchInput.value.toLowerCase();
            const rows = booksTableBody.getElementsByTagName('tr');

            Array.from(rows).forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filter) ? '' : 'none';
            });
        });
    }

    // 5. Dynamic Delete Row Logic
    if (booksTableBody) {
        booksTableBody.addEventListener('click', (e) => {
            if (e.target.closest('.btn-action.delete')) {
                if (confirm('Are you sure you want to delete this book record?')) {
                    const row = e.target.closest('tr');
                    row.remove();
                }
            }
        });
    }

    // 6. Logout Button Logic
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            if (confirm('Are you sure you want to logout?')) {
                window.location.href = 'login.html';
            }
        });
    }
});
/*Book Catalog End*/

/*Member Database Start*/
document.addEventListener('DOMContentLoaded', () => {
    // Modal Elements
    const registerModal = document.getElementById('registerModal');
    const openRegisterModalBtn = document.getElementById('openRegisterModalBtn');
    const closeRegisterModalBtn = document.getElementById('closeRegisterModalBtn');
    const cancelRegisterBtn = document.getElementById('cancelRegisterBtn');
    const registerMemberForm = document.getElementById('registerMemberForm');
    const membersTableBody = document.getElementById('membersTableBody');
    const totalMembersCount = document.getElementById('totalMembersCount');

    // 1. Open Modal
    if (openRegisterModalBtn) {
        openRegisterModalBtn.addEventListener('click', () => {
            registerModal.classList.add('active');
        });
    }

    // 2. Close Modal Functions
    const closeModal = () => {
        registerModal.classList.remove('active');
        registerMemberForm.reset();
    };

    if (closeRegisterModalBtn) closeRegisterModalBtn.addEventListener('click', closeModal);
    if (cancelRegisterBtn) cancelRegisterBtn.addEventListener('click', closeModal);

    // Helper: Avatar Initials Generator
    const getInitials = (name) => {
        const parts = name.trim().split(' ');
        if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
        return name.substring(0, 2).toUpperCase();
    };

    // 3. Register New Member Form Submit
    if (registerMemberForm) {
        registerMemberForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const name = document.getElementById('memberName').value;
            const accountId = document.getElementById('memberAccountId').value;
            const status = document.getElementById('memberStatus').value;

            const initials = getInitials(name);
            
            let statusBadgeClass = 'active-premium';
            if (status === 'Active Standard') statusBadgeClass = 'active-standard';
            if (status === 'Suspended') statusBadgeClass = 'suspended';

            const banBtnHtml = status === 'Suspended'
                ? `<button class="btn-action-sm btn-unban"><i class="fa-solid fa-user-check"></i> Unban</button>`
                : `<button class="btn-action-sm btn-ban"><i class="fa-solid fa-user-slash"></i> Ban</button>`;

            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                <td>
                    <div class="user-cell">
                        <span class="avatar blue">${initials}</span>
                        <span class="user-name">${name}</span>
                    </div>
                </td>
                <td><span class="account-id">${accountId}</span></td>
                <td><span class="badge-status ${statusBadgeClass}">${status}</span></td>
                <td><span class="books-active"><i class="fa-solid fa-book-reader"></i> 0 E-Books</span></td>
                <td class="action-buttons-cell">
                    <button class="btn-action-sm btn-profile"><i class="fa-solid fa-user"></i> Profile</button>
                    <button class="btn-action-sm btn-edit"><i class="fa-solid fa-pen"></i> Edit</button>
                    ${banBtnHtml}
                </td>
            `;

            membersTableBody.appendChild(newRow);

            // Update Metrics Count
            if (totalMembersCount) {
                let currentNum = parseInt(totalMembersCount.textContent.replace(',', '')) || 0;
                totalMembersCount.textContent = (currentNum + 1).toLocaleString();
            }

            closeModal();
            alert(`Reader ${name} registered successfully!`);
        });
    }

    // 4. Live Member Search Filter
    const searchInput = document.getElementById('memberSearchInput');
    if (searchInput) {
        searchInput.addEventListener('keyup', () => {
            const filter = searchInput.value.toLowerCase();
            const rows = membersTableBody.getElementsByTagName('tr');

            Array.from(rows).forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filter) ? '' : 'none';
            });
        });
    }

    // 5. Direct Actions (Ban, Unban, Profile, Edit)
    if (membersTableBody) {
        membersTableBody.addEventListener('click', (e) => {
            const row = e.target.closest('tr');
            const userName = row ? row.querySelector('.user-name')?.textContent : '';

            // Ban Button
            if (e.target.closest('.btn-ban')) {
                if (confirm(`Are you sure you want to ban ${userName}?`)) {
                    const statusBadge = row.querySelector('.badge-status');
                    statusBadge.className = 'badge-status suspended';
                    statusBadge.textContent = 'Suspended';
                    
                    const banBtn = e.target.closest('.btn-ban');
                    banBtn.outerHTML = `<button class="btn-action-sm btn-unban"><i class="fa-solid fa-user-check"></i> Unban</button>`;
                }
            } 
            // Unban Button
            else if (e.target.closest('.btn-unban')) {
                if (confirm(`Unban access for ${userName}?`)) {
                    const statusBadge = row.querySelector('.badge-status');
                    statusBadge.className = 'badge-status active-standard';
                    statusBadge.textContent = 'Active Standard';
                    
                    const unbanBtn = e.target.closest('.btn-unban');
                    unbanBtn.outerHTML = `<button class="btn-action-sm btn-ban"><i class="fa-solid fa-user-slash"></i> Ban</button>`;
                }
            }
            // Profile Button
            else if (e.target.closest('.btn-profile')) {
                alert(`Viewing profile details for ${userName}`);
            }
            // Edit Button
            else if (e.target.closest('.btn-edit')) {
                alert(`Edit reader information for ${userName}`);
            }
        });
    }

    // 6. Logout Logic
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            if (confirm('Are you sure you want to logout?')) {
                window.location.href = 'login.html';
            }
        });
    }
});
/*Member Database End*/

/*Student Messages Start*/
document.addEventListener('DOMContentLoaded', () => {
    // Student Chat Data Store
    const chatData = {
        1: {
            name: "Pyae Phyo Thant",
            id: "#LMS-9841",
            initials: "PT",
            avatarColor: "blue",
            messages: [
                { sender: "incoming", text: "ဆရာရှာ.. Java Advanced programming PDF လင့်ခ်က နှိပ်လိုက်ရင် error ပြပြီး ဒေါင်းလို့မရဖြစ်နေလို့ပါ အကူအညီပေးပါဦး။", time: "" },
                { sender: "outgoing", text: "ဟုတ်ကဲ့ပါ မင်္ဂလာပါ Pyae Phyo Thant ရေ.. Cloud server link ပြင်ပေးထားပါတယ်။ အခု ပြန်ပြီး ဒေါင်းလုဒ်ဆွဲကြည့်ပေးပါဗျ။", time: "11:48 AM • Sent" },
                { sender: "incoming", text: "ဟုတ်ကဲ့ ရပါပြီဆရာ! အခု အဆင်ပြေစွာ ဒေါင်းလို့ရသွားပါပြီ ကျေးဇူးအများကြီးတင်ပါတယ်ဆရာ။", time: "" }
            ]
        },
        2: {
            name: "Khaing Zaw Nyi",
            id: "#LMS-3201",
            initials: "KN",
            avatarColor: "purple",
            messages: [
                { sender: "incoming", text: "မင်္ဂလာပါဆရာ.. Web Development နဲ့ ပတ်သက်တဲ့ စာအုပ်အသစ်တွေ ထပ်တင်ပေးဖို့ မေတ္တာရပ်ခံချင်ပါတယ်။", time: "10m ago" }
            ]
        },
        3: {
            name: "Hnin Yamone Oo",
            id: "#LMS-1104",
            initials: "HO",
            avatarColor: "orange",
            messages: [
                { sender: "incoming", text: "ဆရာရှာ.. သမီး အကောင့် Login ဝင်လို့ မရဖြစ်နေလို့ ပါစကားဝှက် ပြန်လည်ပြင်ဆင်ပေးပါဦး။", time: "1h ago" }
            ]
        }
    };

    let activeChatId = 1;

    // Elements
    const chatList = document.getElementById('chatList');
    const chatBody = document.getElementById('chatBody');
    const activeStudentName = document.getElementById('activeStudentName');
    const activeStudentId = document.getElementById('activeStudentId');
    const activeAvatar = document.getElementById('activeAvatar');
    const sendMessageForm = document.getElementById('sendMessageForm');
    const messageInput = document.getElementById('messageInput');
    const chatSearchInput = document.getElementById('chatSearchInput');

    // Render Active Chat Messages
    function renderChat(id) {
        activeChatId = id;
        const currentData = chatData[id];

        // Update Header
        activeStudentName.textContent = currentData.name;
        activeStudentId.textContent = currentData.id;
        activeAvatar.textContent = currentData.initials;
        activeAvatar.className = `avatar ${currentData.avatarColor}`;

        // Render Messages
        chatBody.innerHTML = '';
        currentData.messages.forEach(msg => {
            const msgDiv = document.createElement('div');
            msgDiv.className = `message-group ${msg.sender}`;

            if (msg.sender === 'incoming') {
                msgDiv.innerHTML = `
                    <div class="avatar ${currentData.avatarColor}">${currentData.initials}</div>
                    <div class="message-content">
                        <p>${msg.text}</p>
                    </div>
                `;
            } else {
                msgDiv.innerHTML = `
                    <div class="message-content">
                        <p>${msg.text}</p>
                        <span class="msg-time">${msg.time || 'Just now • Sent'}</span>
                    </div>
                    <div class="avatar cyan">AD</div>
                `;
            }
            chatBody.appendChild(msgDiv);
        });

        // Scroll to Bottom
        chatBody.scrollTop = chatBody.scrollHeight;
    }

    // Switch Chat Event
    if (chatList) {
        chatList.addEventListener('click', (e) => {
            const chatItem = e.target.closest('.chat-item');
            if (!chatItem) return;

            document.querySelectorAll('.chat-item').forEach(item => item.classList.remove('active'));
            chatItem.classList.add('active');

            const id = chatItem.getAttribute('data-id');
            renderChat(id);
        });
    }

    // Send Message
    if (sendMessageForm) {
        sendMessageForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const text = messageInput.value.trim();
            if (!text) return;

            // Push Message to Active Data
            chatData[activeChatId].messages.push({
                sender: 'outgoing',
                text: text,
                time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' • Sent'
            });

            messageInput.value = '';
            renderChat(activeChatId);
        });
    }

    // Live Search Inbox Filter
    if (chatSearchInput) {
        chatSearchInput.addEventListener('keyup', () => {
            const filter = chatSearchInput.value.toLowerCase();
            const items = chatList.getElementsByClassName('chat-item');

            Array.from(items).forEach(item => {
                const name = item.getAttribute('data-name').toLowerCase();
                item.style.display = name.includes(filter) ? 'flex' : 'none';
            });
        });
    }

    // Broadcast Modal Logic
    const broadcastModal = document.getElementById('broadcastModal');
    const broadcastBtn = document.getElementById('broadcastBtn');
    const closeBroadcastModal = document.getElementById('closeBroadcastModal');
    const cancelBroadcast = document.getElementById('cancelBroadcast');
    const broadcastForm = document.getElementById('broadcastForm');

    if (broadcastBtn) broadcastBtn.addEventListener('click', () => broadcastModal.classList.add('active'));
    
    const closeBroadcast = () => {
        broadcastModal.classList.remove('active');
        broadcastForm.reset();
    };

    if (closeBroadcastModal) closeBroadcastModal.addEventListener('click', closeBroadcast);
    if (cancelBroadcast) cancelBroadcast.addEventListener('click', closeBroadcast);

    if (broadcastForm) {
        broadcastForm.addEventListener('submit', (e) => {
            e.preventDefault();
            alert('Broadcast announcement sent to selected students successfully!');
            closeBroadcast();
        });
    }

    // Block Reader Action
    const blockReaderBtn = document.getElementById('blockReaderBtn');
    if (blockReaderBtn) {
        blockReaderBtn.addEventListener('click', () => {
            const currentName = chatData[activeChatId].name;
            if (confirm(`Are you sure you want to block ${currentName}?`)) {
                alert(`${currentName} has been blocked.`);
            }
        });
    }
});
/*Student Messages End*/

/*Reading Logs Start*/
document.addEventListener("DOMContentLoaded", () => {
  const searchInput = document.getElementById("searchInput");
  const searchBtn = document.getElementById("searchBtn");
  const refreshBtn = document.getElementById("refreshBtn");
  const tableRows = document.querySelectorAll("#logsTableBody tr");

  // Filter logs function
  function filterLogs() {
    const query = searchInput.value.toLowerCase().trim();

    tableRows.forEach(row => {
      const studentName = row.querySelector(".student-info span")?.textContent.toLowerCase() || "";
      const bookTitle = row.querySelector(".book-info strong")?.textContent.toLowerCase() || "";

      if (studentName.includes(query) || bookTitle.includes(query)) {
        row.style.display = "";
      } else {
        row.style.display = "none";
      }
    });
  }

  // Trigger search on button click and keyup
  searchBtn.addEventListener("click", filterLogs);
  searchInput.addEventListener("keyup", filterLogs);

  // Refresh Button action
  refreshBtn.addEventListener("click", () => {
    refreshBtn.classList.add("fa-spin");
    setTimeout(() => {
      alert("Reading logs updated!");
      refreshBtn.classList.remove("fa-spin");
    }, 600);
  });

  // Disconnect button
  const disconnectBtn = document.getElementById("disconnectBtn");
  if (disconnectBtn) {
    disconnectBtn.addEventListener("click", () => {
      if (confirm("Are you sure you want to disconnect from Reader Cafe LMS?")) {
        window.location.href = "login.html"; // Redirect link
      }
    });
  }
});
/*Reading Logs End*/

/*Review Requests Start*/
document.addEventListener("DOMContentLoaded", () => {
  
  // Approve Review Action
  const approveBtns = document.querySelectorAll(".btn-approve");
  approveBtns.forEach(btn => {
    btn.addEventListener("click", function() {
      const card = this.closest(".request-card");
      card.style.transition = "0.4s ease";
      card.style.opacity = "0";
      card.style.transform = "scale(0.95)";
      
      setTimeout(() => {
        card.remove();
        updatePendingCount();
      }, 400);
    });
  });

  // Delete / Dismiss Action
  const deleteBtns = document.querySelectorAll(".btn-delete, .btn-dismiss");
  deleteBtns.forEach(btn => {
    btn.addEventListener("click", function() {
      const card = this.closest(".request-card");
      if (confirm("Are you sure you want to remove this request?")) {
        card.style.transition = "0.4s ease";
        card.style.opacity = "0";
        card.style.transform = "scale(0.95)";
        
        setTimeout(() => {
          card.remove();
          updatePendingCount();
        }, 400);
      }
    });
  });

  // Add To Catalog Action
  const catalogBtns = document.querySelectorAll(".btn-catalog");
  catalogBtns.forEach(btn => {
    btn.addEventListener("click", function() {
      alert("Book request added to Books Catalog queue!");
      const card = this.closest(".request-card");
      card.remove();
      updatePendingCount();
    });
  });

  // Dynamic Pending Counter update function
  function updatePendingCount() {
    const remainingCards = document.querySelectorAll(".request-card").length;
    const pendingBadge = document.querySelector(".pending-badge");
    const sidebarBadge = document.querySelector(".yellow-badge");
    
    if (pendingBadge) {
      pendingBadge.innerHTML = `<i class="fa-solid fa-trophy"></i> ${remainingCards} Pending Reviews`;
    }
    if (sidebarBadge) {
      sidebarBadge.textContent = remainingCards;
    }
  }

  // Disconnect button
  const disconnectBtn = document.getElementById("disconnectBtn");
  if (disconnectBtn) {
    disconnectBtn.addEventListener("click", () => {
      if (confirm("Are you sure you want to disconnect from Reader Cafe LMS?")) {
        window.location.href = "login.html";
      }
    });
  }
});
/*Review Requests End*/

/*Analytics Reports Start*/
document.addEventListener("DOMContentLoaded", () => {
  
  // Render Chart using Chart.js
  const ctx = document.getElementById('engagementChart');

  if (ctx) {
    const gradient = ctx.getContext('2d').createLinearGradient(0, 0, 0, 250);
    gradient.addColorStop(0, 'rgba(0, 242, 195, 0.4)');
    gradient.addColorStop(1, 'rgba(0, 242, 195, 0.0)');

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [{
          label: 'Active Readers',
          data: [250, 320, 420, 410, 520, 650],
          borderColor: '#00f2c3',
          borderWidth: 3,
          pointBackgroundColor: '#00f2c3',
          pointRadius: 4,
          fill: true,
          backgroundColor: gradient,
          tension: 0.4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          }
        },
        scales: {
          x: {
            grid: {
              color: 'rgba(28, 56, 52, 0.5)'
            },
            ticks: {
              color: '#8c9e9b'
            }
          },
          y: {
            min: 200,
            max: 700,
            grid: {
              color: 'rgba(28, 56, 52, 0.5)'
            },
            ticks: {
              color: '#8c9e9b',
              stepSize: 50
            }
          }
        }
      }
    });
  }

  // Export Button functionality
  const exportBtn = document.getElementById("exportBtn");
  if (exportBtn) {
    exportBtn.addEventListener("click", () => {
      alert("Downloading Library Analytics Report (PDF / CSV)...");
    });
  }

  // Disconnect button functionality
  const disconnectBtn = document.getElementById("disconnectBtn");
  if (disconnectBtn) {
    disconnectBtn.addEventListener("click", () => {
      if (confirm("Are you sure you want to disconnect from Reader Cafe LMS?")) {
        window.location.href = "login.html";
      }
    });
  }
});
/*Analytics Reports End*/

/*Control Settings Start*/
document.addEventListener("DOMContentLoaded", () => {
  
  // Slider Realtime Value Updates
  const maxBooksSlider = document.getElementById("maxBooks");
  const maxBooksVal = document.getElementById("maxBooksVal");

  if (maxBooksSlider && maxBooksVal) {
    maxBooksSlider.addEventListener("input", (e) => {
      maxBooksVal.textContent = `${e.target.value} Books`;
    });
  }

  const borrowDaysSlider = document.getElementById("borrowDays");
  const borrowDaysVal = document.getElementById("borrowDaysVal");

  if (borrowDaysSlider && borrowDaysVal) {
    borrowDaysSlider.addEventListener("input", (e) => {
      borrowDaysVal.textContent = `${e.target.value} Days`;
    });
  }

  // Save Settings Button
  const saveBtn = document.getElementById("saveBtn");
  if (saveBtn) {
    saveBtn.addEventListener("click", () => {
      saveBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Saving...`;
      setTimeout(() => {
        alert("All settings updated successfully!");
        saveBtn.innerHTML = `<i class="fa-solid fa-floppy-disk"></i> Save All Changes`;
      }, 600);
    });
  }

  // Backup Action Button
  const backupBtn = document.getElementById("backupBtn");
  if (backupBtn) {
    backupBtn.addEventListener("click", () => {
      alert("Preparing SQL Backup... Download will start shortly.");
    });
  }

  // Disconnect Button
  const disconnectBtn = document.getElementById("disconnectBtn");
  if (disconnectBtn) {
    disconnectBtn.addEventListener("click", () => {
      if (confirm("Are you sure you want to disconnect from Reader Cafe LMS?")) {
        window.location.href = "login.html";
      }
    });
  }
});
/*Control Settings End*/