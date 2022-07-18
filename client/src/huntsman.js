export const getSkills = (skillName, experienceLevel) => {
  return fetch(`http://localhost:8080/skills/${skillName}/${experienceLevel}`, {
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
    },
  })
    .then((response) => response.json())
    .catch(console.log);
};
