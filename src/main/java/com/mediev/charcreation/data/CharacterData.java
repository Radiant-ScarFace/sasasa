package com.mediev.charcreation.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public final class CharacterData {
    private final UUID playerId;
    private final String firstName;
    private final String lastName;
    private final Nationality nationality;
    private final Gender gender;
    private final int birthDay;
    private final int birthMonth;
    private final int birthYear;
    private final Background background;

    public CharacterData(UUID playerId, String firstName, String lastName, Nationality nationality, Gender gender,
                         int birthDay, int birthMonth, int birthYear, Background background) {
        this.playerId = playerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationality = nationality;
        this.gender = gender;
        this.birthDay = birthDay;
        this.birthMonth = birthMonth;
        this.birthYear = birthYear;
        this.background = background;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public Gender getGender() {
        return gender;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public Background getBackground() {
        return background;
    }

    public void writeFields(PacketByteBuf buf) {
        buf.writeString(firstName);
        buf.writeString(lastName);
        buf.writeEnumConstant(nationality);
        buf.writeEnumConstant(gender);
        buf.writeInt(birthDay);
        buf.writeInt(birthMonth);
        buf.writeInt(birthYear);
        buf.writeEnumConstant(background);
    }

    public static CharacterData readFields(PacketByteBuf buf, UUID playerId) {
        String firstName = buf.readString(16);
        String lastName = buf.readString(16);
        Nationality nationality = buf.readEnumConstant(Nationality.class);
        Gender gender = buf.readEnumConstant(Gender.class);
        int birthDay = buf.readInt();
        int birthMonth = buf.readInt();
        int birthYear = buf.readInt();
        Background background = buf.readEnumConstant(Background.class);
        return new CharacterData(playerId, firstName, lastName, nationality, gender, birthDay, birthMonth, birthYear, background);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("PlayerId", playerId);
        nbt.putString("FirstName", firstName);
        nbt.putString("LastName", lastName);
        nbt.putString("Nationality", nationality.name());
        nbt.putString("Gender", gender.name());
        nbt.putInt("BirthDay", birthDay);
        nbt.putInt("BirthMonth", birthMonth);
        nbt.putInt("BirthYear", birthYear);
        nbt.putString("Background", background.name());
        return nbt;
    }

    public static CharacterData fromNbt(NbtCompound nbt) {
        return new CharacterData(
                nbt.getUuid("PlayerId"),
                nbt.getString("FirstName"),
                nbt.getString("LastName"),
                Nationality.valueOf(nbt.getString("Nationality")),
                Gender.valueOf(nbt.getString("Gender")),
                nbt.getInt("BirthDay"),
                nbt.getInt("BirthMonth"),
                nbt.getInt("BirthYear"),
                Background.valueOf(nbt.getString("Background"))
        );
    }
}