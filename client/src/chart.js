import Chart from "chart.js/auto";

const MAX_SIZE = 14;
const backgroundColor = [
  "#0074D9",
  "#FF4136",
  "#2ECC40",
  "#FF851B",
  "#7FDBFF",
  "#B10DC9",
  "#FFDC00",
  "#001f3f",
  "#39CCCC",
  "#01FF70",
  "#85144b",
  "#F012BE",
  "#3D9970",
  "#111111",
  "#AAAAAA",
];

const summarizeSkills = (skills) => {
  const skillsToDisplay = skills.slice(0, MAX_SIZE);
  const other = {
    name: "Other",
    count: skills.slice(MAX_SIZE).reduce((acc, skill) => acc + skill.count, 0),
  };
  return other.count ? [...skillsToDisplay, other] : skillsToDisplay;
};

const createSkillChartData = (skills, title) => {
  return {
    labels: skills.map(({ name }) => name),
    datasets: [
      {
        label: title,
        data: skills.map(({ count }) => count),
        backgroundColor,
        hoverOffset: MAX_SIZE + 1,
      },
    ],
  };
};

export const createSkillChart = (title) => (skills) => {
  console.log({ title, skills });
  const config = {
    type: "pie",
    data: createSkillChartData(summarizeSkills(skills), title),
    options: {
      responsive: true,
      maintainAspectRatio: false,
    },
  };

  return config;
};

let currentChart = null;

export const attachSkillChart = (element) => {
  return (chart) => {
    if (currentChart) {
      currentChart.destroy();
    }

    currentChart = new Chart(element, chart);
  };
};
