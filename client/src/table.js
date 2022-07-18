const table = (className) => (headerRow, dataRows) => {
  const _table = document.createElement("table");
  _table.className = className;
  [headerRow, ...dataRows].forEach((element) => _table.appendChild(element));
  return _table;
};

const tableRow = (elements) => {
  const row = document.createElement("tr");
  elements.forEach((element) => row.appendChild(element));
  return row;
};

const tableCell = (tag) => (value) => {
  const element = document.createElement(tag);
  element.innerText = value;
  return element;
};

const headerCell = tableCell("th");

const dataCell = tableCell("td");

export const createSkillsTable = (tableClassName) => (skills) => {
  const headerRow = tableRow([headerCell("skill"), headerCell("count")]);
  const skillRows = skills.map((skill) =>
    tableRow([dataCell(skill.name), dataCell(skill.count)])
  );
  return table(tableClassName)(headerRow, skillRows);
};

const removeChildren = (element) => {
  while (element.firstChild) {
    element.removeChild(element.lastChild);
  }
};

export const upsertTable = (element) => (table) => {
  removeChildren(element);
  element.appendChild(table);
};
