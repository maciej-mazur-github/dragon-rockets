<h2 align="center">
Simple TDD based library to manage Dragon rockets and space missions. As per requirements, no front-end, no persistence, no frameworks have been used. Just core Java and unit testing.
</h2>

## Table Of Content
<ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#briefly-about-project-requirements">Briefly About Project Requirements</a></li>
      </ul>
    </li>
    <li>
      <a href="#more-details-on-project-mechanisms-and-implementation">More Details On Project Mechanisms And Implementation</a>
      <ul>
        <li><a href="#spring-security-configuration">Spring Security Configuration</a></li>
        <li><a href="#registration-and-validation">Registration And Validation</a></li>
        <li><a href="#admin-panel">Admin Panel</a></li>
        <li><a href="#weather-panel">Weather Panel</a></li>
      </ul>
    </li>
    <li>
      <a href="#project-expansion-plans">Project Expansion Plans</a>
      <ul>
        <li><a href="#file-handling">File Handling</a></li>
        <li><a href="#filtering-offers-by-category">Filtering Offers By Category</a></li>
        <li><a href="#managing-categories-and-offers">Managing Categories And Offers</a></li>
        <li><a href="#unit-testing">Unit testing</a></li>
      </ul>
    </li>
  </ol>

<br><br><br>

## Briefly About Project Requirements

The repository supports the following operations:

1. Add a new rocket. When the new rocket is added, its status should be “On ground” (it’s the initial score).
2. Assign rocket to mission. Rocket can be assigned only to 1 mission.
3. Change rocket status (see list possible statuses below).
4. Add a new mission. When the new mission is added, its status should be “Scheduled”)
5. Assign rockets to the mission (the mission can have multiple rockets)
6. Change mission status
7. Get a summary of missions by number of rockets assigned. Missions with the same number of rockets should be ordered in descending alphabetical order.
8. Statuses of rockets and mission:
<br><br>
   <pre>
    a. Rocket can have statuses:
        i. “On ground” – initial status, where the rocket is not assigned to any mission
        ii. “In space” – the rocket was assigned to the mission
        iii. “In repair” – the rocket is due to repair, it implies “Pending” status of the mission
        iv. “In build” – if you are not a human, add this status
    b. Mission can have statuses:
        i. “Scheduled” – initial status, where no rockets are assigned
        ii. “Pending” – at least one rocket is assigned and one or more assigned rockets are in repair
        iii. “In Progress” – at least one rocket is assigned and none of them is in repair
        iv. “Ended” – the final stage of the mission, at this point rockets should not be assigned anymore to a mission
</pre>

## More Details On Project Mechanisms And Implementation

- It is not possible to add more than one rocket of the same name to the repository (name uniqueness is guaranteed by using rocket names as HashMap keys)
- It is not possible to add more than one mission of the same name (name uniqueness is guaranteed by using mission names as HashMap keys)
- When new mission is added, its status automatically sets to SCHEDULED and it has no rockets assigned
- When new rocket is added to repository, its status is automatically set to ON_GROUND
- It is not possible to add a rocket to a mission without first creating the rocket in repository
- It is not possible to assign a rocket to a mission without first adding the given mission
- It is not possible to change the status of a non-existent rocket
- A rocket can only be not assigned to any mission, or it can be assigned to one mission only. It cannot be assigned to multiple missions at the same time
- Until the rocket is assigned to any mission (i.e. when lastMission field is not null), it is not possible to re-assign it to another mission
- To be able to re-assign a rocket to another mission, you should first ground it (set its status to ON_GROUND) or make the mission, that has this rocket assigned, change its status either to SCHEDULED or ENDED. These two last actions set rocket's lastMission field to null, which makes the rocket re-assignable 
- When rocket's status is set to IN_REPAIR and when it loses the mission assignment (by mission status change to SCHEDULED or ENDED) and when it is re-assigned to another mission, its IN_REPAIR status remains, which can possibly result in automatic change of new mission's status from IN_PROGRESS to PENDING
- When the rocket is not assigned to any mission (when it is ON_GROUND or IN_REPAIR but with lastMission being null) it is not possible to manually change its status without first assigning it to any mission
- Providing the incorrect mission name when using setRocketStatus() method results in operation failure
- Assigning any non-IN_REPAIR rocket to a SCHEDULED mission automatically changes mission's status to IN_PROGRESS and rocket's status to IN_SPACE
- Changing IN_PROGRESS or PENDING mission's status to SCHEDULED results in grounding its rockets that were IN_SPACE at the moment and in nullifying their lastMission field
- Missions that are SCHEDULED or ENDED show zero rockets in the getSummary() result list (hence the nullifying of rocket's lastMission field)
- Rockets with status IN_REPAIR are omitted in the getSummary() result list
- Rockets that are ON_GROUND (but have already been assigned earlier to any mission), as per requirements are no longer assigned to any mission (so they are re-assignable), but still they will be included in their last mission's summary (as per requirements examples) thanks to the information about the last mission being still kept in lastMission field (until it is nullified or overwritten with another mission)
- When a mission has all its rockets in space, but then all of them will be grounded one by one, grounding the last IN_SPACE rocket will result in automatic change of mission's status to SCHEDULED and in automatic nullification of lastMission field in all rockets that had this mission set as lastMission
- When any of mission's rocket's status is set to IN_REPAIR, it automatically changes the mission's status to PENDING. If getSummary() method is called at this point, this IN_REPAIR rocket will be omitted
- Attempt to manually change mission status from IN_PROGRESS to PENDING is ignored (this change only happens when any of the rockets' status is changed to IN_REPAIR)
- The mission that has been ENDED cannot be used anymore, so an attempt to manually change its status back to IN_PROGRESS will be ignored
- An attempt to change mission status manually from SCHEDULED to IN_PROGRESS is ignored (it can only be changed by assigning new rocket to the mission or by switching all of this mission's IN_REPAIR rockets back to IN_SPACE)
- An attempt to add new rocket to the mission that has already been ENDED will be ignored
- When the rocket was assigned to any mission and then grounded, to be able to change its status back to IN_SPACE you need to first re-assign this rocket back to any mission
- Changing mission's status to SCHEDULED will ground all its non-IN_REPAIR rockets and will nullify lastMission fields in all non-IN_REPAIR and IN_REPAIR rockets
- When you set IN_PROGRESS mission's rocket's status to IN_REPAIR, mission status will automatically change to PENDING, but when you later set this IN_REPAIR rocket's status back to IN_SPACE, mission's status will automatically change from PENDING back to IN_PROGRESS
- Grounding one of mission's rockets does not affect other rockets in this mission. 
- Internal processing implementation has been used to make sure about the required order of getSummary() result list. The missions are ordered by the number of their IN_SPACE and ON_GROUND rockets (IN_REPAIR rockets are omitted), descending. Whenever any two or more missions have the same number of rockets, they are ordered by their name, descending. Also, the rocket lists in all missions are ordered, firstly by status (ON_GROUND as first, IN_SPACE as second), secondly by name, ascending. Missions that are SCHEDULED or ENDED will show zero rockets