# Changelog v4.9.0 — Overhaul UI/UX "Dramaku Cinema PRO"

Pembaruan besar desain antarmuka (UI) dan pengalaman pengguna (UX) untuk menghasilkan tampilan aplikasi streaming profesional berekelas bioskop lokal, menghilangkan kesan kaku/template AI.

---

## 1. Palet Warna & Sistem Desain ("Obsidian & Cinema Crimson")
- **Obsidian Dark Theme:** Menggantikan latar hitam datar dengan `0xFF08090E` (Deep Velvet Obsidian), permukaan kartu berlapis kaca frosted (`0xFF131622` & `0xFF1C2132`), dan batas kaca halus (`0x22FFFFFF`).
- **Cinema Crimson Rose:** Menggantikan warna primer merah-muda neon dengan aksen `0xFFE11D48` (khas Netflix/WeTV/Apple TV+), dikombinasikan dengan aksen emas kerajaan `0xFFF59E0B` untuk label VIP/MovieBox dan biru langit `0xFF38BDF8` untuk label Drama Asia.

---

## 2. Beranda Ekosistem Kategori (`CategoryHomeScreen`)
- **Header Kategori PRO:** Ditambahkan badge status `PRO`, sapaan waktu Indonesia yang cerdas (`"🌤️ Selamat Pagi, mau nonton apa hari ini?"`), dan tombol pengaturan bergaya frosted glass.
- **Kartu Portal Sinematik (`CategoryWideCard`):**
  - Dihilangkan strip warna vertikal kiri bergaya template standar.
  - Kartu kini berlatar gradien horizontal halus, emblem ikon kotak bercahaya (`54.dp` rounded 16dp), badge spesifikasi teknis (`"8 PLATFORM • FULL HD • VERTIKAL"`, `"KOREA & CHINA • SUB INDO • ONGOING"`, `"FILM LAYAR LEBAR • BOX OFFICE • 1080P"`), dan tombol navigasi bulat dengan ikon panah bersinar.

---

## 3. Navigasi & Beranda Utama (`HomeScreen` & `BottomNavBar`)
- **Floating Glass Navbar (`BottomNavBar`):** Navigasi bawah kini tampil melayang (floating) dengan efek frosted glass, garis sorot atas halus, dan indikator pil bercahaya pada tab aktif.
- **Cinematic Capsule Filter Chips:** Chip filter platform (Melolo, Drakor, MovieBox, dll) bergaya kapsul dengan gradien merah bata pada tab aktif dan kaca gelap pada tab pasif.
- **Hero Billboard Banner (`HeroCard`):** Banner utama beranda ditinggikan ke `410.dp`, gradien gelap multi-lapis untuk keterbacaan judul super tegas (`31.sp` ExtraBold), badge frosted `PILIHAN UTAMA`, tombol utama `"Putar Sekarang"` (gradient crimson, teks putih tegas), serta tombol `"Simpan"` bergaya kaca.
- **Kartu Poster Sinema (`DiscoverDramaCard`):** Rasio asli poster bioskop (`0.68f`), badge frosted glass platform di sudut kiri atas (`"MELOLO"`, `"DRAKOR"`, `"MOVIEBOX"`) dengan titik warna indikator, dan badge episode di kanan atas.

---

## 4. Halaman Detail, Pemutar & Koleksi
- **Detail Sinematik (`DetailScreen`):** Tombol aksi utama `"Mulai Tonton" / "Lanjut Ep X"` diperbaiki menggunakan teks putih bersih (`Color.White`), menghilangkan kontras buruk teks hitam pada tombol merah. Tombol Favorit (hati merah menyala saat aktif) dan Bagikan kini bergaya frosted glass terintegrasi.
- **Daftar Koleksi (`ListItem`):** Kartu riwayat & favorit bergaya frosted glass dengan poster proporsional dan tombol hapus khusus (`DeleteOutline`) yang jelas & intuitif.
- **Pengaturan (`SettingItem`):** Kartu pengaturan dengan tombol sakelar (Switch) putih di atas trek merah yang kontras.
