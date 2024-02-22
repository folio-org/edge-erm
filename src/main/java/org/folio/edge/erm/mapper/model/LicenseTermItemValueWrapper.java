package org.folio.edge.erm.mapper.model;

import org.folio.erm.domain.dto.LicenseTermItemValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class LicenseTermItemValueWrapper<T> extends LicenseTermItemValue {

  private T value;
}
