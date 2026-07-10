# PDF Viewer & Search App - Android

একটি শক্তিশালী Android অ্যাপ যা PDF ফাইল দেখায় এবং টেক্সট সার্চ করার সুবিধা দেয়। সার্চ করা প্রতিটি শব্দ/লাইন হাইলাইট হয় এবং Next/Previous বাটন দিয়ে নেভিগেট করা যায়।

## 🎯 ফিচার

✅ **PDF Viewing** - 99+ পেজের PDF ফাইল দেখুন  
✅ **Search & Count** - PDF এ টেক্সট খুঁজুন এবং মোট কতটা ফলাফল পাওয়া গেছে তা দেখুন  
✅ **Navigation** - ▲ এবং ▼ বাটন দিয়ে একটি ফলাফল থেকে অন্যটিতে যান  
✅ **Highlight Counter** - বর্তমান ফলাফল নম্বর দেখুন (যেমন: 5 / 23)  
✅ **Zoom Control** - শুধুমাত্র PDF কন্টেন্ট জুম হয়, টুলবার ফিক্সড থাকে  
✅ **Page Indicator** - বর্তমান পেজ নম্বর এবং মোট পেজ সংখ্যা দেখুন  
✅ **Smart Search** - Case-insensitive সার্চ  
✅ **Auto Navigation** - সার্চ করলে স্বয়ংক্রিয়ভাবে প্রথম ফলাফলে যায়  

## 📋 প্রযুক্তিগত বিবরণ

- **Minimum SDK:** 29 (Android 10)
- **Target SDK:** 34 (Android 14)
- **Language:** Kotlin
- **PDF Library:** Android PDF Viewer by barteksc
- **Text Extraction:** PDFBox Android

## 📁 প্রজেক্ট স্ট্রাকচার

```
book-pdf-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/rubelsordar/bookpdfapp/
│   │   │   ├── MainActivity.kt          # মেইন অ্যাক্টিভিটি
│   │   │   └── PDFSearcher.kt           # PDF সার্চ এবং টেক্সট এক্সট্রেকশন
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml    # মেইন লেআউট
│   │   │   ├── drawable/
│   │   │   │   └── search_background.xml # সার্চ বার স্টাইলিং
│   │   │   └── values/
│   │   │       ├── strings.xml          # স্ট্রিং রিসোর্স
│   │   │       ├── colors.xml           # কালার রিসোর্স
│   │   │       └── themes.xml           # থিম
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🚀 ইনস্টলেশন এবং সেটআপ

### ১. রিপোজিটরি ক্লোন করুন
```bash
git clone https://github.com/rubelsordar441-boop/book-pdf-app.git
cd book-pdf-app
```

### ২. Android Studio এ খুলুন
- Android Studio খুলুন
- Open > এই ফোল্ডার সিলেক্ট করুন
- Gradle সিঙ্ক হওয়ার জন্য অপেক্ষা করুন

### ৩. PDF ফাইল যোগ করুন

**দুটি উপায় আছে:**

**অপশন A: assets ফোল্ডার থেকে (সুপারিশকৃত)**
```
app/src/main/assets/
└── document.pdf  (আপনার 99 পেজের PDF)
```

**অপশন B: App নিজেই ফাইল নিবে**
- `app/src/main/java/com/rubelsordar/bookpdfapp/` তে `MainActivity.kt` খুলুন
- লাইন 40 এ পরিবর্তন করুন:
```kotlin
// Assets থেকে পড়বে
val pdfFile = File(filesDir, "document.pdf")

// অথবা সরাসরি দিতে পারেন
val pdfFile = File("/path/to/your/document.pdf")
```

### ৪. অ্যাপ চালু করুন
```bash
# বা Android Studio তে Run বাটন ক্লিক করুন
./gradlew installDebug
```

## 📖 ব্যবহার

1. **অ্যাপ খুলুন** - PDF স্বয়ংক্রিয়ভাবে লোড হবে
2. **সার্চ বারে টাইপ করুন** - যে শব্দ খুঁজতে চান তা লিখুন
3. **ফলাফল গণনা দেখুন** - কতটা ম্যাচ পাওয়া গেছে তা দেখুন
4. **নেভিগেট করুন** - ▲ এবং ▼ বাটন দিয়ে ফলাফলের মধ্যে চলুন
5. **পেজ নম্বর দেখুন** - নিচে বর্তমান পেজ এবং মোট পেজ দেখা যায়

## 🎨 UI/UX বৈশিষ্ট্য

### লেআউট কাঠামো:
- **Toolbar** (Fixed): সার্চ বার - জুম হয় না
- **Navigation Bar** (Fixed): Result counter এবং Next/Previous বাটন - জুম হয় না
- **PDF Viewer** (Zoomable): PDF কন্টেন্ট - জুম হয়
- **Page Indicator** (Fixed): পেজ নম্বর - জুম হয় না

## 🔍 সার্চ ফাংশনালিটি

### PDFSearcher ক্লাস:
- PDF থেকে টেক্সট এক্সট্রেক্ট করে
- প্রতিটি পেজে সার্চ কোয়েরি খুঁজে
- সমস্ত ম্যাচিং রেজাল্ট রিটার্ন করে
- পেজ নম্বর এবং পজিশন ট্র্যাক করে

```kotlin
data class HighlightPosition(
    val pageNumber: Int,      // কোন পেজে আছে
    val text: String,         // ম্যাচড টেক্সট
    val startIndex: Int,      // শুরু অবস্থান
    val endIndex: Int         // শেষ অবস্থান
)
```

## 🔧 কাস্টমাইজেশন

### সার্চ রেজাল্ট সংখ্যা বদলানো
`MainActivity.kt` এ:
```kotlin
val count = "${currentHighlightIndex + 1} / ${highlightPositions.size}"
// আপনার পছন্দমতো ফরম্যাট করুন
```

### বাটন ডিজাইন বদলানো
`activity_main.xml` এ:
```xml
<Button
    android:id="@+id/btnNext"
    android:text="▼"  <!-- এটি পরিবর্তন করুন -->
    android:textColor="#FF6200EE"  <!-- রং পরিবর্তন করুন -->
/>
```

### রঙ পরিবর্তন
`app/src/main/res/values/colors.xml` এ কাস্টম করুন

## 🐛 ট্রাবলশুটিং

### সমস্যা: PDF দেখা যাচ্ছে না
**সমাধান:**
- `document.pdf` ফাইল সঠিক জায়গায় আছে কিনা চেক করুন
- প্রিন্ট করুন:
```kotlin
Log.d("PDF", "File exists: ${pdfFile.exists()}")
```

### সমস্যা: সার্চ ফলাফল দেখা যাচ্ছে না
**সমাধান:**
- PDF টেক্সট বেসড কিনা চেক করুন (Image-based PDF এ সার্চ কাজ করে না)
- PDFBox লাইব্রেরি সঠিকভাবে যোগ করা হয়েছে কিনা চেক করুন

### সমস্যা: অ্যাপ ক্র্যাশ হচ্ছে
**সমাধান:**
- Logcat চেক করুন: `adb logcat`
- রিসোর্স পারমিশন সঠিক আছে কিনা চেক করুন

## 📦 ডিপেন্ডেন্সি

```gradle
// PDF Viewer
implementation("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")

// Text Extraction
implementation("com.tom_roush:pdfbox-android:2.0.27.0")
```

## 🚀 ভবিষ্যতের উন্নতি

- [ ] PDF highlight rendering (বাস্তব হাইলাইটিং)
- [ ] Case sensitive সার্চ অপশন
- [ ] Regex সার্চ সাপোর্ট
- [ ] PDF থেকে টেক্সট কপি করার সুবিধা
- [ ] Dark mode সাপোর্ট
- [ ] পিঞ্চ জুম সাপোর্ট
- [ ] বুকমার্ক ফিচার
- [ ] সার্চ হিস্টরি

## 📝 লাইসেন্স

MIT License - আপনি যেকোনো কাজে ব্যবহার করতে পারবেন

## 👨‍💻 ডেভেলপার

**rubelsordar441-boop**

## 💬 সাপোর্ট

কোনো সমস্যা বা প্রশ্ন থাকলে GitHub Issues এ তুলে ধরুন।

---

**Happy Reading! 📖**
