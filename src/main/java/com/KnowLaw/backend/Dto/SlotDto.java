package com.KnowLaw.backend.Dto;

import com.KnowLaw.backend.Entity.Slots;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotDto {
    private long id;
    private String time;

    public SlotDto(Slots slot){
        id=slot.getId();
        time=slot.getTime();
    }
}
