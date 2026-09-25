package com.example.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.telegram.telegrambots.meta.api.objects.Update;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@Entity
@Table(name = "raw_data")
public class RawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Update event;
}
