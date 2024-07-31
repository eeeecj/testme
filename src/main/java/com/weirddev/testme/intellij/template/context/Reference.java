package com.weirddev.testme.intellij.template.context;

import com.intellij.psi.PsiType;
import com.weirddev.testme.intellij.template.TypeDictionary;
import lombok.Getter;

/**
 * A reference to a defined class construct, other than a method (i.e. field/variable).
 *
 * Date: 06/05/2017
 *
 * @author Yaron Yamin
 *
 */
public class Reference {
    /**
     * name given to reference
     */
    @Getter private final String referenceName;
    /**
     * Type of reference
     */
    @Getter private final Type referenceType;
    /**
     * Type of reference owner class
     */
    @Getter private final Type ownerType;
    private final String referenceId;

    public Reference(String referenceName, PsiType refType, PsiType psiOwnerType, TypeDictionary typeDictionary) {
        this.referenceName = referenceName;
        referenceType = new Type(refType, null, typeDictionary, 1, false);
        ownerType = new Type(psiOwnerType, null, typeDictionary, 1, false);
        referenceId = ownerType.getCanonicalName() + referenceName + referenceType.getCanonicalName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference)) return false;

        Reference reference = (Reference) o;

        return referenceId.equals(reference.referenceId);
    }

    @Override
    public int hashCode() {
        return referenceId.hashCode();
    }

    @Override
    public String toString() {
        return "Reference{" +
                "referenceName='" + referenceName + '\'' +
                ", referenceType=" + referenceType +
                ", ownerType=" + ownerType +
                '}';
    }
}
