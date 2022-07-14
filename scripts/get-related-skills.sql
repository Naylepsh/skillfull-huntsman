SELECT related_reqs.name, COUNT(*) as c
FROM requirements 
JOIN offer_requirements on offer_requirements.requirement_name = requirements.name
JOIN offers on offers.url = offer_requirements.offer_url
JOIN offer_requirements related_ors on related_ors.offer_url = offer_requirements.offer_url
JOIN requirements related_reqs on related_reqs.name = related_ors.requirement_name
WHERE requirements.name = "Scala"
  AND offers.experience_level = "junior"
GROUP BY related_reqs.name
ORDER BY c desc