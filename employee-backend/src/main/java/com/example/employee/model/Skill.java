package com.example.employee.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "skill_master")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "skill_name", unique = true)
    private String skillName;

    @Column(name = "skill_category")
    private String skillCategory;

    @Column(name = "skill_experience_category")
    private String skillExperienceCategory;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // getters & setters

    public Long getSkillId() { return skillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getSkillCategory() { return skillCategory; }
    public void setSkillCategory(String skillCategory) { this.skillCategory = skillCategory; }

    public String getSkillExperienceCategory() { return skillExperienceCategory; }
    public void setSkillExperienceCategory(String skillExperienceCategory) { this.skillExperienceCategory = skillExperienceCategory; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
