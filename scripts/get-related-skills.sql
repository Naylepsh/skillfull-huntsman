SELECT related_reqs.name, COUNT(*) as c
FROM skills 
JOIN offer_skills on offer_skills.skill_name = skills.name
JOIN offers on offers.url = offer_skills.offer_url
JOIN offer_skills related_ors on related_ors.offer_url = offer_skills.offer_url
JOIN skills related_reqs on related_reqs.name = related_ors.skill_name
WHERE skills.name = "Scala"
  AND offers.experience_level = "junior"
GROUP BY related_reqs.name
ORDER BY c desc