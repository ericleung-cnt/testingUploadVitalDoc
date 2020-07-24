package org.mardep.ssrs.domain.lvpfs;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class VmssUpdateVesselParticularReply {

    @Getter
    @Setter
    private String shipId;

    @Getter
    @Setter
    private String timestamp;

    @Getter
    @Setter
    private String message;
}