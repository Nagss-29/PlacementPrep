// ============================================
// PlacementPrep — Result page logic
// ============================================

document.addEventListener("DOMContentLoaded", () => {
    Auth.requireAuth();

    const raw = sessionStorage.getItem("pp_last_result");
    if (!raw) {
        document.getElementById("resultContainer").innerHTML =
            `<p class="text-center text-muted-pp">No recent result found. Take a quiz to see your results here.</p>`;
        return;
    }

    const result = JSON.parse(raw);

    document.getElementById("scoreValue").textContent = `${result.score}/${result.totalQuestions}`;
    document.getElementById("percentageValue").textContent = `${result.percentage.toFixed(1)}%`;
    document.getElementById("correctValue").textContent = result.correctCount;
    document.getElementById("wrongValue").textContent = result.wrongCount;
    document.getElementById("skippedValue").textContent = result.skippedCount;
    document.getElementById("accuracyValue").textContent = `${result.accuracy.toFixed(1)}%`;

    const mins = Math.floor(result.timeTakenSeconds / 60);
    const secs = result.timeTakenSeconds % 60;
    document.getElementById("timeTakenValue").textContent = `${mins}m ${secs}s`;

    renderChart(result);
    renderReview(result.review);
});

function renderChart(result) {
    const ctx = document.getElementById("performanceChart");
    if (!ctx || typeof Chart === "undefined") return;

    new Chart(ctx, {
        type: "doughnut",
        data: {
            labels: ["Correct", "Wrong", "Skipped"],
            datasets: [{
                data: [result.correctCount, result.wrongCount, result.skippedCount],
                backgroundColor: ["#16a34a", "#dc2626", "#f59e0b"],
                borderWidth: 0
            }]
        },
        options: {
            plugins: { legend: { position: "bottom" } },
            cutout: "65%"
        }
    });
}

function renderReview(review) {
    const container = document.getElementById("reviewContainer");
    if (!review || review.length === 0) {
        container.innerHTML = `<p class="text-muted-pp">No questions to review.</p>`;
        return;
    }

    container.innerHTML = review.map((r, i) => {
        const statusClass = r.selectedAnswer === null || r.selectedAnswer === undefined
            ? "review-skipped"
            : (r.correct ? "review-correct" : "review-wrong");

        const statusLabel = r.selectedAnswer === null || r.selectedAnswer === undefined
            ? "Skipped"
            : (r.correct ? "Correct" : "Wrong");

        return `
        <div class="card-pp ${statusClass} mb-3">
            <div class="card-pp-body">
                <div class="d-flex justify-content-between mb-2">
                    <strong>Q${i + 1}. ${r.questionText}</strong>
                    <span class="badge bg-secondary">${statusLabel}</span>
                </div>
                <div class="small">
                    <div>A. ${r.optionA}</div>
                    <div>B. ${r.optionB}</div>
                    <div>C. ${r.optionC}</div>
                    <div>D. ${r.optionD}</div>
                </div>
                <div class="mt-2 small">
                    <strong>Your answer:</strong> ${r.selectedAnswer || "Not answered"} &nbsp;|&nbsp;
                    <strong>Correct answer:</strong> ${r.correctAnswer}
                </div>
                ${r.explanation ? `<div class="mt-2 small text-muted-pp"><strong>Explanation:</strong> ${r.explanation}</div>` : ""}
            </div>
        </div>`;
    }).join("");
}
