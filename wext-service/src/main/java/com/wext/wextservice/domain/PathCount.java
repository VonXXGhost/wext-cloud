package com.wext.wextservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SqlResultSetMapping(
        name="pathCountMapping",
        entities = {
                @EntityResult(
                        entityClass = PathCount.class,
                        fields = {
                                @FieldResult(name = "fullPath", column = "full_path"),
                                @FieldResult(name = "count", column = "count_number")
                        }
                )
        }
)
@NamedNativeQuery(
        name = "PathCount.getHotChildPath",
        query = "SELECT DISTINCT w.full_path as full_path, count(full_path) as count_number\n" +
                "FROM wext w\n" +
                "WHERE w.created_time > TIMESTAMPADD(HOUR, -:hours, now())\n" +
                "  AND w.full_path REGEXP concat('^', :parentPath, '/[^/]+$')\n" +
                "GROUP BY w.full_path\n" +
                "ORDER BY count(full_path) DESC",
        resultSetMapping = "pathCountMapping"
)
public class PathCount {
    @Id
    private String fullPath;
    private Long count;
}
