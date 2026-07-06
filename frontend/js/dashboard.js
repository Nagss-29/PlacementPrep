// ============================================
// PlacementPrep — Dashboard page logic
// ============================================

document.addEventListener("DOMContentLoaded", async () => {
    Auth.requireAuth();
    const user = Auth.getUser();
    document.getElementById("welcomeName").textContent = user?.name || "Student";

    try {
        const stats = await apiRequest("/dashboard");

        document.getElementById("statTotalTests").textContent = stats.totalTests;
        document.getElementById("statHighestScore").textContent = stats.highestScore;
        document.getElementById("statAvgScore").textContent = stats.averageScore.toFixed(1);
        document.getElementById("statAccuracy").textContent = stats.accuracy.toFixed(1) + "%";
        document.getElementById("statStreak").textContent = stats.currentStreak;
        document.getElementById("statAttempted").textContent = stats.questionsAttempted;

        const activityList = document.getElementById("recentActivityList");
        activityList.innerHTML = "";

        if (!stats.recentActivities || stats.recentActivities.length === 0) {
            activityList.innerHTML = `<p class="text-muted-pp">No quizzes attempted yet. Start practicing to see your activity here!</p>`;
        } else {
            stats.recentActivities.forEach(a => {
                const date = new Date(a.date).toLocaleDateString();
                activityList.innerHTML += `
                    <div class="d-flex justify-content-between align-items-center py-2 border-bottom">
                        <div>
                            <strong>${a.category}</strong>
                            <div class="text-muted-pp small">${date}</div>
                        </div>
                        <div class="text-end">
                            <span class="fw-bold">${a.score}/${a.totalQuestions}</span>
                            <div class="text-muted-pp small">${a.percentage.toFixed(1)}%</div>
                        </div>
                    </div>`;
            });
        }
    } catch (err) {
        showToast(err.message, "error");
    }
});
