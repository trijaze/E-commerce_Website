import { useEffect } from 'react';
import Chart from 'chart.js/auto';

export default function Reports() {
  useEffect(() => {
    const ctx = document.getElementById('reportChart');
    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4'],
        datasets: [{
          label: 'Doanh thu (triệu VNĐ)',
          data: [120, 90, 140, 100],
          backgroundColor: '#0066cc',
        }]
      }
    });
  }, []);

  return (
    <div>
      <h2>Thống kê & Báo cáo</h2>
      <canvas id="reportChart" width="400" height="200"></canvas>
    </div>
  );
}
