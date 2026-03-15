document.addEventListener('DOMContentLoaded', function() {
    // ========== DEĞİŞKENLER ==========
    const token = localStorage.getItem('token');
    const userData = JSON.parse(localStorage.getItem('user') || '{}');
    const apiBaseUrl = '/rest/api';

    // Token kontrolü
    if (!token || isTokenExpired(token)) {
        logout();
        return;
    }

    // DOM elementleri
    const memberSearch = document.getElementById('memberSearch');
    const filterRole = document.getElementById('filterRole');
    const bookForm = document.getElementById('addBookForm');
    const bookTableBody = document.querySelector('#bookTable tbody');
    const memberTableBody = document.querySelector('#memberTable tbody');
    const logoutBtn = document.getElementById('logoutBtn');
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    const membersTabBtn = document.querySelector('[data-tab="members"]');
    const allLoansTabBtn = document.querySelector('[data-tab="all-loans"]');
    const editProfileBtn = document.getElementById('editProfileBtn');
    const myLoansSearch = document.getElementById('myLoansSearch');
    const loanSearch = document.getElementById('loanSearch');
    const filterStatus = document.getElementById('filterStatus');
    const bookSearch = document.getElementById('bookSearch');
    const searchCategory = document.getElementById('searchCategory');

    // Veri depolama
    let allBooks = [];
    let allLoans = [];
    let myLoans = [];

    // ========== YARDIMCI FONKSİYONLAR ==========
    function isTokenExpired(token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 < Date.now();
        } catch (e) {
            return true;
        }
    }

    function logout() {
        localStorage.clear();
        window.location.href = '/login.html';
    }

    async function apiFetch(endpoint, options = {}) {
        const url = endpoint.startsWith('http') ? endpoint : `${apiBaseUrl}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` }),
            ...options.headers
        };

        const response = await fetch(url, { ...options, headers });
        return handleResponse(response);
    }

    async function handleResponse(response) {
        if (!response.ok) {
            let errorMessage = `HTTP error! status: ${response.status}`;
            try {
                const errorData = await response.json();
                errorMessage = errorData.errorMessage || errorData.message || errorMessage;
            } catch (e) {
                // ignore
            }
            throw new Error(errorMessage);
        }
        // 204 No Content için boş dönebilir
        if (response.status === 204) {
            return null;
        }
        const data = await response.json();
        // Eğer response bir obje ve "payload" alanı varsa onu döndür, yoksa direkt data
        return data && typeof data === 'object' && 'payload' in data ? data.payload : data;
    }

    function showLoading(show) {
        let loader = document.getElementById('loadingSpinner');
        if (!loader) {
            loader = document.createElement('div');
            loader.id = 'loadingSpinner';
            loader.style.cssText = `
                display: none;
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                padding: 20px;
                background: rgba(0,0,0,0.8);
                color: white;
                border-radius: 10px;
                z-index: 9999;
            `;
            loader.textContent = 'Yükleniyor...';
            document.body.appendChild(loader);
        }
        loader.style.display = show ? 'block' : 'none';
    }

    function showSuccess(message) {
        alert('✓ ' + message);
    }

    function showError(message) {
        console.error(message);
        alert('❌ ' + message);
    }

    function formatDate(dateString) {
        if (!dateString) return '-';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('tr-TR', {
                year: 'numeric', month: 'long', day: 'numeric'
            });
        } catch (e) {
            return dateString;
        }
    }

    function escapeHtml(text) {
        if (!text) return text;
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // ========== YETKİ KONTROLÜ ==========
    if (userData.role !== 'ADMIN') {
        if (bookForm) bookForm.style.display = 'none';
        if (membersTabBtn) membersTabBtn.style.display = 'none';
        if (allLoansTabBtn) allLoansTabBtn.style.display = 'none';
    }

    // ========== SEKMELER ==========
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabId = button.getAttribute('data-tab');

            tabButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');

            tabContents.forEach(content => content.classList.remove('active'));
            const targetTab = document.getElementById(`${tabId}-tab`);
            if (targetTab) targetTab.classList.add('active');

            switch(tabId) {
                case 'profile': loadProfile(); break;
                case 'books': loadBooks(); break;
                case 'members': if (userData.role === 'ADMIN') loadMembers(); break;
                case 'all-loans': if (userData.role === 'ADMIN') loadAllLoans(); break;
            }
        });
    });

    // ========== EVENT LİSTENERLAR ==========
    if (bookForm) {
        bookForm.addEventListener('submit', saveBook);
    }
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
    if (editProfileBtn) {
        editProfileBtn.addEventListener('click', openProfileEditModal);
    }
    if (bookSearch) {
        bookSearch.addEventListener('input', filterBooks);
    }
    if (searchCategory) {
        searchCategory.addEventListener('change', filterBooks);
    }
    if (memberSearch) {
        memberSearch.addEventListener('input', filterMembers);
    }
    if (filterRole) {
        filterRole.addEventListener('change', filterMembers);
    }
    if (loanSearch) {
        loanSearch.addEventListener('input', filterLoans);
    }
    if (filterStatus) {
        filterStatus.addEventListener('change', filterLoans);
    }
    if (myLoansSearch) {
        myLoansSearch.addEventListener('input', filterMyLoans);
    }

    // İlk yükleme
    loadProfile();

    // ========== KİTAP FONKSİYONLARI ==========
    async function loadBooks() {
        try {
            showLoading(true);
            allBooks = await apiFetch('/book/get/list') || [];
            renderBooks(allBooks);
        } catch (error) {
            showError('Kitaplar yüklenirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    function filterBooks() {
        const searchTerm = bookSearch?.value.toLowerCase() || '';
        const categoryFilter = searchCategory?.value || '';
        const rows = document.querySelectorAll('#bookTable tbody tr');
        let visibleCount = 0;

        rows.forEach(row => {
            if (row.cells.length < 6) return;
            const title = row.cells[1]?.textContent.toLowerCase() || '';
            const author = row.cells[2]?.textContent.toLowerCase() || '';
            const category = row.cells[3]?.textContent.toUpperCase() || '';
            const isbn = row.cells[4]?.textContent.toLowerCase() || '';

            const matchesSearch = searchTerm === '' || title.includes(searchTerm) || author.includes(searchTerm) || isbn.includes(searchTerm);
            const matchesCategory = categoryFilter === '' || category === categoryFilter;

            row.style.display = (matchesSearch && matchesCategory) ? '' : 'none';
            if (matchesSearch && matchesCategory) visibleCount++;
        });

        updateBookSearchResultsCount(visibleCount);
    }

    function updateBookSearchResultsCount(visibleCount) {
        const totalCount = allBooks.length;
        const searchHeader = document.querySelector('#books-tab h3');
        if (searchHeader) {
            searchHeader.innerHTML = `Kitap Listesi <span style="font-size:14px;color:#666;margin-left:10px;">(${visibleCount}/${totalCount} kitap)</span>`;
        }
    }

    function renderBooks(books) {
        if (!bookTableBody) return;
        bookTableBody.innerHTML = '';

        if (!books || books.length === 0) {
            bookTableBody.innerHTML = '<tr><td colspan="7" style="text-align:center;">Kitap bulunamadı.</td></tr>';
            return;
        }

        books.forEach(book => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${book.id || '-'}</td>
                <td>${escapeHtml(book.title || '-')}</td>
                <td>${escapeHtml(book.author || '-')}</td>
                <td>${book.category || '-'}</td>
                <td>${book.isbnNo || '-'}</td>
                <td>${book.available ? 'Evet' : 'Hayır'}</td>
                <td>
                    ${userData.role === 'ADMIN' ? `<button class="btn-delete" data-id="${book.id}">Sil</button>` : ''}
                    ${book.available ? `<button class="btn-borrow" data-id="${book.id}">Ödünç Al</button>` : '-'}
                </td>
            `;
            bookTableBody.appendChild(row);
        });

        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', () => deleteBook(btn.dataset.id));
        });
        document.querySelectorAll('.btn-borrow').forEach(btn => {
            btn.addEventListener('click', () => borrowBook(btn.dataset.id));
        });
    }

    async function saveBook(e) {
        e.preventDefault();
        const formData = {
            title: document.getElementById('bookTitle')?.value.trim(),
            author: document.getElementById('bookAuthor')?.value.trim(),
            category: document.getElementById('bookCategory')?.value,
            isbnNo: document.getElementById('bookIsbn')?.value.trim()
        };

        if (!formData.title || !formData.author || !formData.category || !formData.isbnNo) {
            showError('Lütfen tüm alanları doldurun.');
            return;
        }

        try {
            showLoading(true);
            await apiFetch('/book/save', {
                method: 'POST',
                body: JSON.stringify(formData)
            });
            showSuccess('Kitap başarıyla eklendi!');
            bookForm.reset();
            loadBooks();
        } catch (error) {
            showError('Kitap eklenirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    async function deleteBook(bookId) {
        if (!confirm('Bu kitabı silmek istediğinize emin misiniz?')) return;
        try {
            showLoading(true);
            await apiFetch(`/book/delete/${bookId}`, { method: 'DELETE' });
            showSuccess('Kitap silindi!');
            loadBooks();
        } catch (error) {
            showError('Kitap silinirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    async function borrowBook(bookId) {
        if (!confirm('Bu kitabı ödünç almak istediğinize emin misiniz?')) return;

        let memberId = userData.memberId;
        if (!memberId) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                memberId = payload.memberId;
            } catch (e) {
                // ignore
            }
        }
        if (!memberId) {
            showError('Üye bilgileriniz eksik. Lütfen tekrar giriş yapın.');
            return;
        }

        try {
            showLoading(true);
            await apiFetch('/loan/borrow', {
                method: 'POST',
                body: JSON.stringify({ bookId: parseInt(bookId), memberId: parseInt(memberId) })
            });
            showSuccess('Kitap ödünç alındı!');
            loadBooks();
            loadProfile();
        } catch (error) {
            showError('Ödünç alma hatası: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    // ========== ÜYE FONKSİYONLARI ==========
    async function loadMembers() {
        if (userData.role !== 'ADMIN') return;
        try {
            showLoading(true);
            const members = await apiFetch('/member/get/list') || [];
            renderMembers(members);
        } catch (error) {
            showError('Üyeler yüklenirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    function filterMembers() {
        const searchText = memberSearch?.value.toLowerCase() || '';
        const roleValue = filterRole?.value || '';
        const rows = document.querySelectorAll('#memberTable tbody tr');

        rows.forEach(row => {
            if (row.cells.length < 7) return;
            const name = row.cells[1]?.textContent.toLowerCase() || '';
            const email = row.cells[2]?.textContent.toLowerCase() || '';
            const phone = row.cells[3]?.textContent.toLowerCase() || '';
            const role = row.cells[5]?.textContent || '';

            const matchesSearch = searchText === '' || name.includes(searchText) || email.includes(searchText) || phone.includes(searchText);
            const matchesRole = roleValue === '' || role === roleValue;

            row.style.display = (matchesSearch && matchesRole) ? '' : 'none';
        });
    }

    function renderMembers(members) {
        if (!memberTableBody || userData.role !== 'ADMIN') return;
        memberTableBody.innerHTML = '';

        if (!members || members.length === 0) {
            memberTableBody.innerHTML = '<tr><td colspan="7" style="text-align:center;">Üye bulunamadı.</td></tr>';
            return;
        }

        members.forEach(member => {
            const userIdForDelete = member.user?.id || member.id;
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${member.id || '-'}</td>
                <td>${escapeHtml(member.fullName || '-')}</td>
                <td>${escapeHtml(member.email || '-')}</td>
                <td>${member.phoneNumber || '-'}</td>
                <td>${escapeHtml(member.user?.username || '-')}</td>
                <td>${member.user?.role || '-'}</td>
                <td>
                    <button class="btn-edit-role" data-id="${member.id}">Rol Düzenle</button>
                    <button class="btn-delete" data-id="${userIdForDelete}">Sil</button>
                </td>
            `;
            memberTableBody.appendChild(row);
        });

        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', () => deleteUser(btn.dataset.id));
        });
        document.querySelectorAll('.btn-edit-role').forEach(btn => {
            btn.addEventListener('click', () => editMemberRole(btn.dataset.id));
        });
    }

    async function deleteUser(userId) {
        if (!confirm('Bu kullanıcıyı silmek istediğinize emin misiniz?')) return;
        try {
            showLoading(true);
            await apiFetch(`/user/delete/${userId}`, { method: 'DELETE' });
            showSuccess('Kullanıcı silindi!');
            loadMembers();
        } catch (error) {
            showError('Kullanıcı silinirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    async function editMemberRole(memberId) {
        try {
            showLoading(true);
            const member = await apiFetch(`/member/get/${memberId}`);
            openRoleEditModal(member);
        } catch (error) {
            showError('Üye bilgileri alınırken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    function openRoleEditModal(member) {
        const existingModal = document.getElementById('editRoleModal');
        if (existingModal) existingModal.remove();

        const modalHtml = `
            <div class="modal" id="editRoleModal">
                <div class="modal-content">
                    <span class="close">&times;</span>
                    <h3>Rol Düzenle</h3>
                    <form id="editRoleForm">
                        <input type="hidden" id="editMemberId" value="${member.id}">
                        <div class="form-group"><label>Kullanıcı: ${escapeHtml(member.user?.username || '-')}</label></div>
                        <div class="form-group"><label>Mevcut Rol: ${member.user?.role || '-'}</label></div>
                        <div class="form-group">
                            <label for="newRole">Yeni Rol:</label>
                            <select id="newRole" required>
                                <option value="USER" ${member.user?.role === 'USER' ? 'selected' : ''}>USER</option>
                                <option value="ADMIN" ${member.user?.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                            </select>
                        </div>
                        <button type="submit">Güncelle</button>
                    </form>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        const modal = document.getElementById('editRoleModal');
        const closeBtn = modal.querySelector('.close');
        const editForm = document.getElementById('editRoleForm');

        modal.style.display = 'block';

        closeBtn.onclick = () => modal.remove();
        window.onclick = (event) => { if (event.target === modal) modal.remove(); };

        editForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            await updateMemberRole(member.id);
            modal.remove();
        });
    }

    async function updateMemberRole(memberId) {
        try {
            showLoading(true);
            const newRole = document.getElementById('newRole').value;
            await apiFetch(`/member/update-role/${memberId}`, {
                method: 'PUT',
                body: JSON.stringify(newRole) // Role enum olarak gönderiliyor
            });
            showSuccess('Rol güncellendi!');
            loadMembers();
        } catch (error) {
            showError('Rol güncellenirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    // ========== ÖDÜNÇ FONKSİYONLARI ==========
    async function loadAllLoans() {
        if (userData.role !== 'ADMIN') return;
        try {
            showLoading(true);
            allLoans = await apiFetch('/loan/all') || [];
            renderAllLoans(allLoans);
        } catch (error) {
            showError('Ödünç listesi yüklenirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    function filterLoans() {
        const searchTerm = loanSearch?.value.toLowerCase() || '';
        const statusFilter = filterStatus?.value || '';
        const rows = document.querySelectorAll('#allLoansTable tbody tr');

        rows.forEach(row => {
            if (row.cells.length < 7) return;
            const memberName = row.cells[0]?.textContent.toLowerCase() || '';
            const bookTitle = row.cells[1]?.textContent.toLowerCase() || '';
            const bookAuthor = row.cells[2]?.textContent.toLowerCase() || '';
            const status = row.cells[6]?.textContent || '';

            const matchesSearch = searchTerm === '' || memberName.includes(searchTerm) || bookTitle.includes(searchTerm) || bookAuthor.includes(searchTerm);
            const matchesStatus = statusFilter === '' || status === statusFilter;

            row.style.display = (matchesSearch && matchesStatus) ? '' : 'none';
        });
    }

    function renderAllLoans(loans) {
        const tbody = document.querySelector('#allLoansTable tbody');
        if (!tbody || userData.role !== 'ADMIN') return;
        tbody.innerHTML = '';

        if (!loans || loans.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">Ödünç alınan kitap bulunamadı.</td></tr>';
            return;
        }

        loans.forEach(loan => {
            const status = loan.returnDate ? 'İade Edildi' : 'Ödünç Alındı';
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${escapeHtml(loan.member?.fullName || '-')}</td>
                <td>${escapeHtml(loan.book?.title || '-')}</td>
                <td>${escapeHtml(loan.book?.author || '-')}</td>
                <td>${formatDate(loan.loanDate)}</td>
                <td>${formatDate(loan.dueDate)}</td>
                <td>${formatDate(loan.returnDate)}</td>
                <td>${status}</td>
            `;
            tbody.appendChild(row);
        });
    }

    // ========== PROFİL FONKSİYONLARI ==========
    async function loadProfile() {
        showLoading(true);
        try {
            const memberData = await apiFetch('/member/me');
            renderProfile(memberData);
        } catch (error) {
            console.error('Profil bilgileri alınamadı:', error);
            showError('Profil bilgileri alınamadı: ' + error.message);
            renderProfile(null); // boş göster
        }

        try {
            myLoans = await apiFetch('/loan/my-loans') || [];
            renderMyLoans(myLoans);
        } catch (error) {
            console.error('Ödünç kitaplar alınamadı:', error);
            showError('Ödünç kitaplarınız alınamadı: ' + error.message);
            renderMyLoans([]);
        } finally {
            showLoading(false);
        }
    }

    function renderProfile(member) {
        const profileDiv = document.getElementById('profileInfo');
        if (!profileDiv) return;

        if (!member) {
            profileDiv.innerHTML = '<p style="color:red;">Profil bilgileri yüklenemedi.</p>';
            return;
        }

        profileDiv.innerHTML = `
        <p><strong>Ad Soyad:</strong> ${escapeHtml(member.fullName || '-')}</p>
        <p><strong>Email:</strong> ${escapeHtml(member.email || '-')}</p>
        <p><strong>Telefon:</strong> ${member.phoneNumber || '-'}</p>
        <p><strong>Üyelik Tarihi:</strong> ${formatDate(member.membershipDate)}</p>
        <p><strong>Kullanıcı Adı:</strong> ${escapeHtml(member.user?.username || '-')}</p>
        <p><strong>Rol:</strong> ${member.user?.role || '-'}</p>
    `;
    }

    function renderMyLoans(loans) {
        const tbody = document.querySelector('#myLoansTable tbody');
        if (!tbody) return;

        const activeLoans = loans.filter(loan => !loan.returnDate);
        tbody.innerHTML = '';

        if (activeLoans.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Ödünç alınan kitap bulunamadı.</td></tr>';
            updateMyLoansSearchResults();
            return;
        }

        activeLoans.forEach(loan => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${escapeHtml(loan.book?.title || '-')}</td>
                <td>${escapeHtml(loan.book?.author || '-')}</td>
                <td>${formatDate(loan.loanDate)}</td>
                <td>${formatDate(loan.dueDate)}</td>
                <td><button class="btn-return" data-id="${loan.id}">İade Et</button></td>
            `;
            tbody.appendChild(row);
        });

        document.querySelectorAll('.btn-return').forEach(btn => {
            btn.addEventListener('click', () => returnBook(btn.dataset.id));
        });

        updateMyLoansSearchResults();
    }

    function filterMyLoans() {
        const searchTerm = myLoansSearch?.value.toLowerCase() || '';
        const rows = document.querySelectorAll('#myLoansTable tbody tr');
        let visibleCount = 0;

        rows.forEach(row => {
            if (row.cells.length < 2) return;
            const title = row.cells[0]?.textContent.toLowerCase() || '';
            const author = row.cells[1]?.textContent.toLowerCase() || '';
            const matches = searchTerm === '' || title.includes(searchTerm) || author.includes(searchTerm);
            row.style.display = matches ? '' : 'none';
            if (matches) visibleCount++;
        });

        updateMyLoansSearchResults(visibleCount);
    }

    function updateMyLoansSearchResults(visibleCount) {
        const searchTerm = myLoansSearch?.value || '';
        const rows = document.querySelectorAll('#myLoansTable tbody tr');
        const totalRows = Array.from(rows).filter(row =>
            !row.innerHTML.includes('Ödünç alınan kitap bulunamadı')
        ).length;
        const loansHeader = document.querySelector('#profile-tab h3:nth-of-type(2)');
        if (!loansHeader) return;

        if (searchTerm) {
            loansHeader.innerHTML = `Ödünç Aldığım Kitaplar <span style="font-size:14px;color:#666;margin-left:10px;">(${visibleCount || 0}/${totalRows} kitap)</span>`;
        } else {
            loansHeader.innerHTML = `Ödünç Aldığım Kitaplar <span style="font-size:14px;color:#666;margin-left:10px;">(${totalRows} kitap)</span>`;
        }
    }

    async function returnBook(loanId) {
        if (!confirm('Bu kitabı iade etmek istediğinize emin misiniz?')) return;
        try {
            showLoading(true);
            await apiFetch(`/loan/return/${loanId}`, { method: 'POST' });
            showSuccess('Kitap iade edildi!');
            loadProfile();
            loadBooks();
        } catch (error) {
            showError('İade hatası: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    // ========== PROFİL DÜZENLEME ==========
    async function openProfileEditModal() {
        try {
            const member = await apiFetch('/member/me');
            showProfileEditModal(member);
        } catch (error) {
            showError('Profil bilgileri alınırken hata oluştu: ' + error.message);
        }
    }

    function showProfileEditModal(member) {
        const existingModal = document.getElementById('editProfileModal');
        if (existingModal) existingModal.remove();

        const modalHtml = `
            <div class="modal" id="editProfileModal">
                <div class="modal-content">
                    <span class="close">&times;</span>
                    <h3>Profili Düzenle</h3>
                    <form id="editProfileForm">
                        <input type="hidden" id="editMemberId" value="${member.id}">
                        <div class="form-group">
                            <label>Ad Soyad:</label>
                            <input type="text" id="editFullName" value="${escapeHtml(member.fullName || '')}" required>
                        </div>
                        <div class="form-group">
                            <label>Email:</label>
                            <input type="email" id="editEmail" value="${escapeHtml(member.email || '')}" required>
                        </div>
                        <div class="form-group">
                            <label>Telefon:</label>
                            <input type="text" id="editPhoneNumber" value="${member.phoneNumber || ''}" required>
                        </div>
                        <button type="submit">Güncelle</button>
                    </form>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        const modal = document.getElementById('editProfileModal');
        const closeBtn = modal.querySelector('.close');
        const editForm = document.getElementById('editProfileForm');

        modal.style.display = 'block';

        closeBtn.onclick = () => modal.remove();
        window.onclick = (event) => { if (event.target === modal) modal.remove(); };

        editForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            await updateProfile(member.id);
            modal.remove();
        });
    }

    async function updateProfile(memberId) {
        const formData = {
            fullName: document.getElementById('editFullName')?.value.trim(),
            email: document.getElementById('editEmail')?.value.trim(),
            phoneNumber: document.getElementById('editPhoneNumber')?.value.trim()
        };

        try {
            showLoading(true);
            await apiFetch(`/member/update/${memberId}`, {
                method: 'PUT',
                body: JSON.stringify(formData)
            });
            showSuccess('Profil güncellendi!');
            loadProfile();
        } catch (error) {
            showError('Profil güncellenirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }
});