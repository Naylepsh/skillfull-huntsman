import { getSkills } from "./huntsman";
import { createSkillChart, attachSkillChart } from "./chart";

const chartElement = document.getElementById("myChart");
const skillForm = document.getElementById("skill-form");

skillForm.onsubmit = (event) => {
  event.preventDefault();

  const skill = document.getElementById("skill").value;
  const experienceLevel = document.getElementById("expLevel").value;

  getSkills(skill, experienceLevel)
    .then(createSkillChart("Related skills"))
    .then(attachSkillChart(chartElement));
};
