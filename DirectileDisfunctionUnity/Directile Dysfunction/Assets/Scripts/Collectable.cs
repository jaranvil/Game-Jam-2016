using UnityEngine;
using System.Collections;
using System;

public class Collectable : MonoBehaviour
{
    private float bonus = 2;

    public float getBonus()
    {
        return bonus;
    }
    public void setBonus(float bonus)
    {
        this.bonus = bonus;
    }
    void OnTriggerEnter(Collider other)
    {
        if (other.gameObject.CompareTag("Player"))
        {
            //gameObject.SetActive(false);
            gameObject.SetActive(false);
            GameManager.instance.AdjustPurpose(bonus);
            GameManager.instance.Message("I know what I must do ! ..I think");
        }
    }
}
