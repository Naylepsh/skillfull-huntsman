import { getSkills } from "./api";
import { createSkillChart, attachSkillChart } from "./chart";
import { createSkillsTable, upsertTable } from "./table";

const chartElement = document.getElementById("myChart");
const skillForm = document.getElementById("skill-form");
const skillTableParent = document.getElementById("skill-table-container");

skillForm.onsubmit = (event) => {
  event.preventDefault();

  const skill = document.getElementById("skill").value;
  const experienceLevel = document.getElementById("expLevel").value;

  const skills = getSkills(skill, experienceLevel);

  skills
    .then(createSkillChart("Related skills"))
    .then(attachSkillChart(chartElement));

  skills
    .then(createSkillsTable("table my-3"))
    .then(upsertTable(skillTableParent));
};
