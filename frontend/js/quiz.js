// ============================================
// PlacementPrep — Quiz page logic
// ============================================

let questions = [];
let currentIndex = 0;
let answers = {};       // questionId -> "A"/"B"/"C"/"D"
let bookmarked = new Set();
let startTime = null;
let timerInterval = null;
let secondsElapsed = 0;
let quizDurationSeconds = 0; // 0 = no auto-submit limit

document.addEventListener("DOMContentLoaded", async () => {
    Auth.requireAuth();

    const params = new URLSearchParams(window.location.search);
    const category = params.get("category") || "Quantitative Aptitude";
    const difficulty = params.get("difficulty") || "";
    const limit = params.get("limit") || 10;

    document.getElementById("quizCategoryLabel").textContent = category;

    try {
        questions = await apiRequest(
            `/quiz/questions?category=${encodeURIComponent(category)}` +
            (difficulty ? `&difficulty=${difficulty}` : "") +
            `&limit=${limit}`
        );

        if (!questions || questions.length === 0) {
            showToast("No questions available for this category yet.", "error");
            return;
        }

        // 90 seconds per question, auto-submit when time runs out
        quizDurationSeconds = questions.length * 90;

        buildPalette();
        renderQuestion(0);
        startTimer();

    } catch (err) {
        showToast(err.message, "error");
    }

    document.getElementById("prevBtn").addEventListener("click", () => navigate(-1));
    document.getElementById("nextBtn").addEventListener("click", () => navigate(1));
    document.getElementById("skipBtn").addEventListener("click", () => { answers[currentQ().id] = null; navigate(1); });
    document.getElementById("bookmarkBtn").addEventListener("click", toggleBookmark);
    document.getElementById("submitBtn").addEventListener("click", () => confirmSubmit());
});

function currentQ() {
    return questions[currentIndex];
}

function renderQuestion(index) {
    currentIndex = index;
    const q = questions[index];

    document.getElementById("questionNumber").textContent = `Question ${index + 1} of ${questions.length}`;
    document.getElementById("questionText").textContent = q.questionText;

    const optionsHtml = ["A", "B", "C", "D"].map(letter => {
        const text = q[`option${letter}`];
        const selected = answers[q.id] === letter ? "selected" : "";
        return `<div class="quiz-option ${selected}" data-letter="${letter}" onclick="selectOption('${letter}')">
                    <strong>${letter}.</strong> ${text}
                </div>`;
    }).join("");
    document.getElementById("optionsContainer").innerHTML = optionsHtml;

    document.getElementById("bookmarkBtn").classList.toggle("btn-pp-primary", bookmarked.has(q.id));
    document.getElementById("bookmarkBtn").classList.toggle("btn-pp-outline", !bookmarked.has(q.id));

    document.getElementById("prevBtn").disabled = index === 0;
    document.getElementById("nextBtn").style.display = index === questions.length - 1 ? "none" : "inline-block";
    document.getElementById("submitBtn").style.display = index === questions.length - 1 ? "inline-block" : "none";

    updateProgressBar();
    updatePalette();
}

function selectOption(letter) {
    answers[currentQ().id] = letter;
    renderQuestion(currentIndex);
}

function navigate(direction) {
    const next = currentIndex + direction;
    if (next >= 0 && next < questions.length) {
        renderQuestion(next);
    }
}

function toggleBookmark() {
    const id = currentQ().id;
    if (bookmarked.has(id)) bookmarked.delete(id);
    else bookmarked.add(id);
    renderQuestion(currentIndex);
}

function buildPalette() {
    const container = document.getElementById("questionPalette");
    container.innerHTML = questions.map((q, i) =>
        `<button class="palette-btn" id="palette-${i}" onclick="renderQuestion(${i})">${i + 1}</button>`
    ).join("");
}

function updatePalette() {
    questions.forEach((q, i) => {
        const btn = document.getElementById(`palette-${i}`);
        btn.className = "palette-btn";
        if (i === currentIndex) btn.classList.add("current");
        if (answers[q.id] !== undefined && answers[q.id] !== null) btn.classList.add("answered");
        else if (answers[q.id] === null) btn.classList.add("skipped");
        if (bookmarked.has(q.id)) btn.classList.add("bookmarked");
    });
}

function updateProgressBar() {
    const answeredCount = Object.keys(answers).length;
    const pct = (answeredCount / questions.length) * 100;
    document.getElementById("progressBar").style.width = `${pct}%`;
}

function startTimer() {
    startTime = Date.now();
    timerInterval = setInterval(() => {
        secondsElapsed++;
        const remaining = quizDurationSeconds - secondsElapsed;

        if (remaining <= 0) {
            clearInterval(timerInterval);
            showToast("Time's up! Auto-submitting your quiz...", "error");
            submitQuiz();
            return;
        }

        const mins = Math.floor(remaining / 60).toString().padStart(2, "0");
        const secs = (remaining % 60).toString().padStart(2, "0");
        document.getElementById("timerDisplay").textContent = `${mins}:${secs}`;
    }, 1000);
}

function confirmSubmit() {
    const unanswered = questions.length - Object.values(answers).filter(v => v !== undefined && v !== null).length;
    const message = unanswered > 0
        ? `You have ${unanswered} unanswered question(s). Submit anyway?`
        : "Submit your quiz now?";

    if (confirm(message)) {
        submitQuiz();
    }
}

async function submitQuiz() {
    clearInterval(timerInterval);

    const params = new URLSearchParams(window.location.search);
    const category = params.get("category") || "Quantitative Aptitude";

    try {
        const result = await apiRequest("/quiz/submit", {
            method: "POST",
            body: {
                category,
                answers,
                timeTakenSeconds: secondsElapsed
            }
        });

        sessionStorage.setItem("pp_last_result", JSON.stringify(result));
        window.location.href = "result.html";
    } catch (err) {
        showToast(err.message, "error");
    }
}
