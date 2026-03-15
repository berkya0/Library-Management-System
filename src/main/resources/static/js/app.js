document.addEventListener("DOMContentLoaded", () => {
    console.log("App.js loaded");

    // Login Form
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const username = document.getElementById("username")?.value.trim();
            const password = document.getElementById("password")?.value;

            if (!username || !password) {
                alert("Kullanıcı adı ve şifre boş olamaz!");
                return;
            }

            try {
                const response = await fetch("/rest/api/authenticate", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, password })
                });

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.errorMessage || `Giriş başarısız! (${response.status})`);
                }

                const data = await response.json();
                // Beklenen yapı: { accessToken, refreshToken, username, role, memberId } (direkt olarak)
                // Eğer data.payload varsa, onu kullan
                const payload = data.payload || data;

                if (!payload.accessToken || !payload.refreshToken) {
                    throw new Error("Sunucudan geçersiz yanıt alındı.");
                }

                localStorage.setItem("token", payload.accessToken);
                localStorage.setItem("refreshToken", payload.refreshToken);
                localStorage.setItem("user", JSON.stringify({
                    username: payload.username,
                    role: payload.role,
                    memberId: payload.memberId
                }));

                window.location.href = "/dashboard.html";
            } catch (err) {
                console.error("Login hatası:", err);
                alert("Giriş başarısız: " + err.message);
            }
        });
    }

    // Register Form
    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const formData = {
                username: document.getElementById("regUsername")?.value.trim(),
                password: document.getElementById("regPassword")?.value,
                fullName: document.getElementById("fullName")?.value.trim(),
                email: document.getElementById("email")?.value.trim(),
                phoneNumber: document.getElementById("phoneNumber")?.value.trim()
            };

            for (let [key, value] of Object.entries(formData)) {
                if (!value) {
                    alert("Lütfen tüm alanları doldurun!");
                    return;
                }
            }

            try {
                const response = await fetch("/rest/api/user/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(formData)
                });

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.errorMessage || `Kayıt başarısız! (${response.status})`);
                }

                // Başarılı kayıt
                alert("Kayıt başarılı! Giriş yapabilirsiniz.");
                window.location.href = "/login.html";
            } catch (err) {
                console.error("Register hatası:", err);
                alert("Kayıt başarısız: " + err.message);
            }
        });
    }
});