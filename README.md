# AlexGoogleMapsCompose

## 專案簡介

**AlexGoogleMapsCompose** 是一個以 **Jetpack Compose + Google Maps Compose** 為核心的 Android 地圖示範專案，完整展示如何在現代 Android 架構下，實作：

* 地圖顯示與 Marker 管理
* 目前位置取得（FusedLocationProviderClient）
* Runtime Permission 與系統定位狀態檢查
* 使用者自訂 Marker 新增流程
* 客製化 InfoWindow（完全不依賴 Google Map 內建樣式）

---
## 📸 Demo

|                       |
|-----------------------|
| ![](docs/demo.png) |

---
## 📱 技術選型

* **Language**：Kotlin
* **UI**：Jetpack Compose / Material 3
* **Map**：Google Maps Compose
* **Architecture**：Clean Architecture（Domain / Data / Presentation）
* **DI**：Hilt
* **Async**：Kotlin Coroutines / Flow
* **Location**：FusedLocationProviderClient

---

## 🏗 專案架構

```
com.alex.yang.alexmaptagscompose
│
├── App.kt
├── MainActivity.kt
│
├── core/
│   └── ContextExtensions.kt
│
├── domain/
│   ├── model/
│   │   └── Place.kt
│   ├── repository/
│   │   ├── PlaceRepository.kt
│   │   ├── LocationRepository.kt
│   │   └── LocationPermissionChecker.kt
│   └── usecase/
│       ├── ObservePlacesUseCase.kt
│       └── GetCurrentLocationUseCase.kt
│
├── data/
│   └── repository/
│       ├── FakePlaceRepository.kt
│       ├── LocationRepositoryImpl.kt
│       └── LocationPermissionCheckerImpl.kt
│
├── presentation/
│   ├── di/
│   │   ├── FakeModule.kt
│   │   └── LocationModule.kt
│   ├── component/
│   │   ├── AddMarkerDialog.kt
│   │   └── PlaceInfoWindow.kt
│   ├── MapViewModel.kt
│   └── MapScreen.kt
│
└── ui/theme/
```

---

## 核心設計說明

### 1️⃣ Clean Architecture 分層

* **Domain Layer**

    * 定義 `Place`、Repository Interface、UseCase
    * 完全不依賴 Android API

* **Data Layer**

    * 實作 Location / Fake Data Repository
    * 封裝 Google Play Services 與權限邏輯

* **Presentation Layer**

    * Compose UI + ViewModel
    * 僅透過 UseCase 存取資料

此設計確保：

* 高可測試性
* 易於替換資料來源（Fake → Real API）
* 架構清晰，適合中大型專案擴充

---

### 2️⃣ Location 取得流程設計

1. UI 層檢查 Runtime Permission
2. 檢查系統定位服務是否啟用
3. 呼叫 `GetCurrentLocationUseCase`
4. Repository 使用 `suspendCancellableCoroutine`
5. 回傳 `Result<Place>`，由 ViewModel 統一處理錯誤

> 此設計可避免 UI 直接依賴 Android API，並能清楚區分錯誤來源（權限 / 系統 / API）。

---

### 3️⃣ Marker 與 InfoWindow 設計

* 使用 **Google Maps Compose Marker**
* 點擊 Marker 時：

    * Animate Camera
    * 延遲顯示自製 `PlaceInfoWindow`
* InfoWindow 為純 Compose UI：

    * Card + Custom Triangle
    * 完全繞過 Google Map 內建限制

優點：

* 樣式完全可控
* 可支援複雜互動（Button / State）

---

### 4️⃣ 使用者新增 Marker 流程

1. 點擊 Floating Toolbar「定位」按鈕
2. 取得目前位置
3. 顯示 `AddMarkerDialog`
4. 輸入名稱 / 描述
5. 新增至 `userMarkers` 狀態
6. 立即顯示於地圖與 InfoWindow

---

## 🎨 UI / UX 特點

* Floating Toolbar 操作
* Loading Overlay 遮罩
* Snackbar 錯誤提示
* 地圖移動時自動關閉 InfoWindow
* Light / Dark Mode Preview

---

## 🚀 適合延伸的功能

* 即時定位追蹤（Continuous Location Updates）
* Marker 本地儲存（Room / DataStore）
* Marker 群組 / 分類
* 導航路線顯示
* 後端 API 串接

---

# 👤 Author

**Alex Yang**  
Android Engineer  
🌐 GitHub: https://github.com/m9939418
